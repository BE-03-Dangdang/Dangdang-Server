package com.dangdang.server.controller;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureRestDocs
@AutoConfigureMockMvc
class ConnectionAccountDatabaseControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Test
  void addConnectionAccount() throws Exception {
    mockMvc.perform(
            post("/connection-account")
//                .header("authentication", )
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
        )
        .andExpect(status().isOk())
        .andDo(
            document(
                "ConnectionAccountDatabaseController/addConnectionAccount",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
//                requestHeaders(
//                    headerWithName("authentication").description("jwt header")
//                ),
                requestFields(
                    fieldWithPath("bankAccountId").type(JsonFieldType.NUMBER).description("은행계좌 Id")
                )
            )
        );
  }
}