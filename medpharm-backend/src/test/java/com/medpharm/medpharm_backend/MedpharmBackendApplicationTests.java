package com.medpharm.medpharm_backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medpharm.repository.MedicamentoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class MedpharmBackendApplicationTests {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;
    @Autowired MedicamentoRepository medicamentos;

    private String token(String usuario) throws Exception {
        String respuesta = mvc.perform(post("/api/v1/auth/login").contentType("application/json")
                .content("{\"username\":\"" + usuario + "\",\"password\":\"password123\"}"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        return "Bearer " + json.readTree(respuesta).get("token").asText();
    }

    private String receta(int cantidad) {
        return "{\"pacienteNombre\":\"Paciente Prueba\",\"detalles\":[{\"medicamentoId\":1,\"cantidad\":"
                + cantidad + ",\"dosisIndicada\":\"Una cada 8 horas\"}]}";
    }

    @Test void rechazaSinTokenYTokenInvalido() throws Exception {
        mvc.perform(get("/api/v1/recetas")).andExpect(status().isUnauthorized());
        mvc.perform(get("/api/v1/recetas").header("Authorization", "Bearer invalido"))
                .andExpect(status().isUnauthorized());
    }

    @Test void loginYConsultas() throws Exception {
        String jwt = token("medico1");
        mvc.perform(get("/api/v1/recetas").header("Authorization", jwt))
                .andExpect(status().isOk()).andExpect(jsonPath("$[0].medico.password").doesNotExist());
        mvc.perform(get("/api/v1/medicamentos").header("Authorization", jwt))
                .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(5));
        mvc.perform(get("/api/v1/recetas/estado/PENDIENTE").header("Authorization", jwt))
                .andExpect(status().isOk()).andExpect(jsonPath("$[0].estado").value("PENDIENTE"));
        token("farma1");
        mvc.perform(post("/api/v1/auth/login").contentType("application/json")
                .content("{\"username\":\"medico1\",\"password\":\"incorrecta\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test void preflightPermiteAuthorization() throws Exception {
        mvc.perform(options("/api/v1/recetas").header("Origin", "http://localhost:4200")
                .header("Access-Control-Request-Method", "POST")
                .header("Access-Control-Request-Headers", "authorization,content-type"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:4200"));
    }

    @Test void crearYCancelarDevuelveStock() throws Exception {
        String jwt = token("medico1");
        int stock = medicamentos.findById(1L).orElseThrow().getStock();
        String respuesta = mvc.perform(post("/api/v1/recetas").header("Authorization", jwt)
                .contentType("application/json").content(receta(2)))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.estado").value("PENDIENTE"))
                .andReturn().getResponse().getContentAsString();
        long id = json.readTree(respuesta).get("id").asLong();
        assertEquals(stock - 2, medicamentos.findById(1L).orElseThrow().getStock());
        mvc.perform(patch("/api/v1/recetas/" + id + "/estado").header("Authorization", token("farma1"))
                .contentType("application/json").content("{\"estado\":\"CANCELADA\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.estado").value("CANCELADA"));
        assertEquals(stock, medicamentos.findById(1L).orElseThrow().getStock());
        mvc.perform(patch("/api/v1/recetas/" + id + "/estado").header("Authorization", jwt)
                .contentType("application/json").content("{\"estado\":\"DESPACHADA\"}"))
                .andExpect(status().isConflict());
    }

    @Test void rechazaCantidadYStockInvalidos() throws Exception {
        String jwt = token("medico1");
        mvc.perform(post("/api/v1/recetas").header("Authorization", jwt)
                .contentType("application/json").content(receta(0)))
                .andExpect(status().isBadRequest());
        mvc.perform(post("/api/v1/recetas").header("Authorization", jwt)
                .contentType("application/json").content(receta(100000)))
                .andExpect(status().isConflict()).andExpect(content().contentTypeCompatibleWith("application/problem+json"));
        mvc.perform(post("/api/v1/recetas").header("Authorization", token("farma1"))
                .contentType("application/json").content(receta(1)))
                .andExpect(status().isForbidden());
    }

    @Test void despacharYValidarEstado() throws Exception {
        String jwt = token("farma1");
        mvc.perform(patch("/api/v1/recetas/1/estado").header("Authorization", jwt)
                .contentType("application/json").content("{\"estado\":\"OTRO\"}"))
                .andExpect(status().isBadRequest());
        mvc.perform(patch("/api/v1/recetas/1/estado").header("Authorization", jwt)
                .contentType("application/json").content("{\"estado\":\"DESPACHADA\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.estado").value("DESPACHADA"));
        mvc.perform(patch("/api/v1/recetas/99999/estado").header("Authorization", jwt)
                .contentType("application/json").content("{\"estado\":\"DESPACHADA\"}"))
                .andExpect(status().isNotFound());
    }
}
