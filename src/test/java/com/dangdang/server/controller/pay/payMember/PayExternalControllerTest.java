package com.dangdang.server.controller.pay.payMember;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.dangdang.server.controller.pay.TestHelper;
import com.dangdang.server.domain.pay.daangnpay.domain.payMember.application.PayMemberService;
import com.dangdang.server.domain.pay.daangnpay.domain.payMember.dto.PayRequest;
import com.dangdang.server.domain.pay.daangnpay.domain.payMember.dto.PayResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import javax.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureRestDocs
@AutoConfigureMockMvc
@SpringBootTest
@Transactional
public class PayExternalControllerTest extends TestHelper {

  @MockBean
  PayMemberService payMemberService;
  @Autowired
  MockMvc mockMvc;
  @Autowired
  ObjectMapper objectMapper;

  String accessToken;
  String bankAccountNumber = "12354324534";

  @BeforeEach
  void setUp() {
    accessToken = jwtSetUp();
  }

  @Test
  @DisplayName("당근머니 충전 API 성공")
  void charge() throws Exception {
    PayRequest payRequest = new PayRequest(
        "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOiIxMTAxMDIwODUzIiwic2NvcGUiOlsiaW5xdWlyeSIsImxvZ2luIiwidHJhbnNmZXIiXSwiaXNzIjoiaHR0cHM6Ly93d3cub3BlbmJhbmtpbmcub3Iua3IiLCJleHAiOjE2ODMwMjMyOTAsImp0aSI6ImZkMTIzNjk0LTI2OWItNDM2Yy04YTYzLTA5OTJhNjkyNzU0YSJ9.Ld3-I6TF4lySzNhrk_mVcxKqWC-JSMRBZYqnysxJoxY",
        bankAccountNumber, 10000);
    String json = objectMapper.writeValueAsString(payRequest);
    PayResponse payResponse = new PayResponse("신한은행", "234716230423", 20000, LocalDateTime.now());

    doReturn(payResponse).when(payMemberService).charge(any(), any());

    mockMvc.perform(patch("/pay-members/money/charge").header("AccessToken", accessToken)
            .contentType(MediaType.APPLICATION_JSON).characterEncoding("UTF-8").content(json))
        .andExpect(status().isOk()).andDo(
            document("PayExternalController/charge", preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestHeaders(headerWithName("AccessToken").description("jwt header")), requestFields(
                    fieldWithPath("openBankingToken").type(JsonFieldType.STRING)
                        .description("openAPI 액세스 토큰"),
                    fieldWithPath("bankAccountNumber").type(JsonFieldType.STRING)
                        .description("은행 계좌 번호"),
                    fieldWithPath("amount").type(JsonFieldType.NUMBER).description("충전 금액")),
                responseFields(
                    fieldWithPath("bank").type(JsonFieldType.STRING).description("출금 계좌 은행명"),
                    fieldWithPath("accountNumber").type(JsonFieldType.STRING).description("출금 계좌번호"),
                    fieldWithPath("money").type(JsonFieldType.NUMBER).description("당근머니 잔액"),
                    fieldWithPath("createdAt").type(JsonFieldType.STRING).description("거래시간"))));
  }
}
