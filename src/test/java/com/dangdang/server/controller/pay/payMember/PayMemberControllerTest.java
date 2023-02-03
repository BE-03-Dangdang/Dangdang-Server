package com.dangdang.server.controller.pay.payMember;

import static com.dangdang.server.global.exception.ExceptionCode.BINDING_WRONG;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
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
import com.dangdang.server.domain.pay.daangnpay.domain.payMember.dto.PostPayMemberSignupResponse;
import com.dangdang.server.domain.pay.daangnpay.domain.payMember.dto.ReceiveRequest;
import com.dangdang.server.domain.pay.daangnpay.domain.payMember.dto.ReceiveResponse;
import com.dangdang.server.domain.pay.daangnpay.domain.payMember.dto.RemittanceRequest;
import com.dangdang.server.domain.pay.daangnpay.domain.payMember.dto.RemittanceResponse;
import com.dangdang.server.domain.pay.daangnpay.domain.payMember.exception.PasswordSizeException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import javax.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MissingServletRequestParameterException;

@AutoConfigureRestDocs
@AutoConfigureMockMvc
@SpringBootTest
@Transactional
class PayMemberControllerTest extends TestHelper {

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
    PayRequest payRequest = new PayRequest(null, bankAccountNumber, 10000);
    String json = objectMapper.writeValueAsString(payRequest);
    PayResponse payResponse = new PayResponse("신한은행", "234716230423", 20000, LocalDateTime.now());

    doReturn(payResponse).when(payMemberService).charge(any(), any());

    mockMvc.perform(
            post("/pay-members/money/charge")
                .header("AccessToken", accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(json)
        )
        .andExpect(status().isOk())
        .andDo(
            document(
                "PayMemberController/charge",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestHeaders(
                    headerWithName("AccessToken").description("jwt header")
                ),
                requestFields(
                    fieldWithPath("openBankingToken").type(JsonFieldType.STRING)
                        .description("openAPI 액세스 토큰").optional(),
                    fieldWithPath("bankAccountNumber").type(JsonFieldType.STRING)
                        .description("충전 계좌 번호"),
                    fieldWithPath("amount").type(JsonFieldType.NUMBER).description("충전 금액")
                ),
                responseFields(
                    fieldWithPath("bank").type(JsonFieldType.STRING).description("출금 계좌 은행명"),
                    fieldWithPath("accountNumber").type(JsonFieldType.STRING)
                        .description("출금 계좌번호"),
                    fieldWithPath("money").type(JsonFieldType.NUMBER).description("당근머니 잔액"),
                    fieldWithPath("createdAt").type(JsonFieldType.STRING).description("거래시간")
                )
            )
        );
  }

  @Test
  @DisplayName("당근머니 출금 API 성공")
  void withdraw() throws Exception {
    PayRequest payRequest = new PayRequest(null, bankAccountNumber, 10000);
    String json = objectMapper.writeValueAsString(payRequest);
    PayResponse payResponse = new PayResponse("신한은행", "234716230423", 20000, LocalDateTime.now());

    doReturn(payResponse).when(payMemberService).withdraw(any(), any());

    mockMvc.perform(
            post("/pay-members/money/withdraw")
                .header("AccessToken", accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(json)
        )
        .andExpect(status().isOk())
        .andDo(
            document(
                "PayMemberController/withdraw",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestHeaders(
                    headerWithName("AccessToken").description("jwt header")
                ),
                requestFields(
                    fieldWithPath("openBankingToken").type(JsonFieldType.STRING)
                        .description("openAPI 액세스 토큰").optional(),
                    fieldWithPath("bankAccountNumber").type(JsonFieldType.STRING)
                        .description("출금 계좌 번호"),
                    fieldWithPath("amount").type(JsonFieldType.NUMBER).description("충전 금액")
                ),
                responseFields(
                    fieldWithPath("bank").type(JsonFieldType.STRING).description("출금 계좌 은행명"),
                    fieldWithPath("accountNumber").type(JsonFieldType.STRING)
                        .description("출금 계좌번호"),
                    fieldWithPath("money").type(JsonFieldType.NUMBER).description("당근머니 잔액"),
                    fieldWithPath("createdAt").type(JsonFieldType.STRING).description("거래시간")
                )
            )
        );
  }

  @Nested
  @DisplayName("당근페이 가입 API는")
  class Signup {

    @Nested
    @DisplayName("empty 혹은 길이가 맞지 않는 값이 들어오면")
    class EmptyOrMinSize {

      @ParameterizedTest
      @EmptySource
      @DisplayName("BadRequest를 응답한다")
      void failInquiryReceiveTest(String input) throws Exception {
        mockMvc.perform(
                post("/pay-members")
                    .header("AccessToken", accessToken)
                    .param("password", input)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("UTF-8")
            )
            .andExpect(status().isBadRequest())
            .andExpect(
                response -> assertTrue(
                    response.getResolvedException() instanceof PasswordSizeException));
      }
    }

    @Nested
    @DisplayName("null 값이 들어오면")
    class Null {

      @ParameterizedTest
      @NullSource
      @DisplayName("BadRequest를 응답한다")
      void failInquiryReceiveTest(String input) throws Exception {
        mockMvc.perform(
                post("/pay-members")
                    .header("AccessToken", accessToken)
                    .param("password", input)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("UTF-8")
            )
            .andExpect(status().isBadRequest())
            .andExpect(
                response -> assertTrue(
                    response.getResolvedException() instanceof MissingServletRequestParameterException));
      }
    }

    @Nested
    @DisplayName("유효한 요청값이 들어오면")
    class Success {

      @Test
      @DisplayName("payMemberId를 반환한다.")
      void successSignup() throws Exception {
        PostPayMemberSignupResponse postPayMemberSignupResponse = PostPayMemberSignupResponse.from(
            1L);
        doReturn(postPayMemberSignupResponse).when(payMemberService).signup(any(), any());

        mockMvc.perform(
                post("/pay-members")
                    .header("AccessToken", accessToken)
                    .param("password", "password123")
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("UTF-8")
            )
            .andExpect(status().isOk())
            .andDo(
                document(
                    "PayMemberController/signup",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestHeaders(
                        headerWithName("AccessToken").description("jwt header")
                    ),
                    responseFields(
                        fieldWithPath("payMemberId").type(JsonFieldType.NUMBER)
                            .description("payMemberId")
                    )
                )
            );
      }
    }
  }

  @Nested
  @DisplayName("수취 조회 API는")
  class InquiryReceive {

    @Nested
    @DisplayName("입금요청 금액이 1원 미만이면")
    class DepositAmountSmallerThanMin {

      @Test
      @DisplayName("BadRequest를 응답한다")
      void failInquiryReceiveTest() throws Exception {
        ReceiveRequest receiveRequest = new ReceiveRequest(null, 0, bankAccountNumber, "097");
        String json = objectMapper.writeValueAsString(receiveRequest);
        String message = BINDING_WRONG.getMessage();

        mockMvc.perform(
                post("/pay-members/inquiry/receive")
                    .header("AccessToken", accessToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("UTF-8")
                    .content(json)
            )
            .andExpect(status().isBadRequest())
            .andExpect(
                response -> assertTrue(response.getResolvedException() instanceof BindException))
            .andExpect(
                response -> assertEquals(message, response.getResponse().getContentAsString()));
      }
    }

    @Nested
    @DisplayName("null 혹은 empty 값이 들어오면")
    class NullOrEmpty {

      @ParameterizedTest
      @NullAndEmptySource
      @DisplayName("BadRequest를 응답한다")
      void failInquiryReceiveTest(String input) throws Exception {
        ReceiveRequest receiveRequest = new ReceiveRequest(null, 0, input, input);
        String json = objectMapper.writeValueAsString(receiveRequest);
        String message = BINDING_WRONG.getMessage();

        mockMvc.perform(
                post("/pay-members/inquiry/receive")
                    .header("AccessToken", accessToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("UTF-8")
                    .content(json)
            )
            .andExpect(status().isBadRequest())
            .andExpect(
                response -> assertTrue(response.getResolvedException() instanceof BindException))
            .andExpect(
                response -> assertEquals(message, response.getResponse().getContentAsString()));
      }
    }

    @Nested
    @DisplayName("bankCode가 3자리가 아니면")
    class BankCodeLengthError {

      @Test
      @DisplayName("BadRequest를 응답한다")
      void failInquiryReceiveTest() throws Exception {
        ReceiveRequest receiveRequest = new ReceiveRequest(null, 0, bankAccountNumber, "4");
        String json = objectMapper.writeValueAsString(receiveRequest);
        String message = BINDING_WRONG.getMessage();

        mockMvc.perform(
                post("/pay-members/inquiry/receive")
                    .header("AccessToken", accessToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("UTF-8")
                    .content(json)
            )
            .andExpect(status().isBadRequest())
            .andExpect(
                response -> assertTrue(response.getResolvedException() instanceof BindException))
            .andExpect(
                response -> assertEquals(message, response.getResponse().getContentAsString()));
      }
    }

    @Nested
    @DisplayName("유효한 요청값이 들어오면")
    class Success {

      @Test
      @DisplayName("수취 조회 정보를 반환한다.")
      void successInquiryReceiveTest() throws Exception {
        ReceiveRequest receiveRequest = new ReceiveRequest(null, 10000, bankAccountNumber, "097");
        String json = objectMapper.writeValueAsString(receiveRequest);
        ReceiveResponse receiveResponse = new ReceiveResponse("홍길동", false, "케이뱅크", "3274623",
            10000,
            0,
            3);

        doReturn(receiveResponse).when(payMemberService).inquiryReceive(any(), any());

        mockMvc.perform(
                post("/pay-members/inquiry/receive")
                    .header("AccessToken", accessToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("UTF-8")
                    .content(json)
            )
            .andExpect(status().isOk())
            .andDo(
                document("PayMemberController/inquiryReceive",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestHeaders(
                        headerWithName("AccessToken").description("jwt header")
                    ),
                    requestFields(
                        fieldWithPath("openBankingToken").type(JsonFieldType.STRING)
                            .description("openAPI 액세스 토큰").optional(),
                        fieldWithPath("depositAmount").type(JsonFieldType.NUMBER)
                            .description("입금 요청 금액"),
                        fieldWithPath("bankAccountNumber").type(JsonFieldType.STRING)
                            .description("수취 조회 계좌 번호"),
                        fieldWithPath("bankCode").type(JsonFieldType.STRING)
                            .description("수취 조회 계좌 은행 코드")
                    ),
                    responseFields(
                        fieldWithPath("receiveClientName").type(JsonFieldType.STRING)
                            .description("수취 계좌 예금주명"),
                        fieldWithPath("isMyAccount").type(JsonFieldType.BOOLEAN)
                            .description("수취 계좌 본인 여부"),
                        fieldWithPath("chargeAccountBankName").type(JsonFieldType.STRING)
                            .description("충전계좌 은행"),
                        fieldWithPath("chargeAccountNumber").type(JsonFieldType.STRING)
                            .description("충전계좌 번호"),
                        fieldWithPath("autoChargeAmount").type(JsonFieldType.NUMBER)
                            .description("자동충전 금액"),
                        fieldWithPath("feeAmount").type(JsonFieldType.NUMBER).description("수수료 금액"),
                        fieldWithPath("freeMonthlyFeeCount").type(JsonFieldType.NUMBER)
                            .description("남은 무료 수수료 횟수")
                    )
                ));
      }
    }
  }

  @Nested
  @DisplayName("송금 API는")
  class Remittance {

    @Nested
    @DisplayName("유효한 요청값이 들어오면")
    class Success {

      @Test
      @DisplayName("당근머니 송금을 진행한다.")
      void successRemittanceTest() throws Exception {
        RemittanceRequest remittanceRequest = new RemittanceRequest(null, 10000, "홍길동",
            bankAccountNumber, "097");
        String json = objectMapper.writeValueAsString(remittanceRequest);
        RemittanceResponse remittanceResponse = new RemittanceResponse("케이뱅크", "3274623",
            1000, 0, 3, 0);

        doReturn(remittanceResponse).when(payMemberService).remittance(any(), any());

        mockMvc.perform(
                post("/pay-members/money/remittance")
                    .header("AccessToken", accessToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("UTF-8")
                    .content(json)
            )
            .andExpect(status().isOk())
            .andDo(
                document("PayMemberController/remittance",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestHeaders(
                        headerWithName("AccessToken").description("jwt header")
                    ),
                    requestFields(
                        fieldWithPath("openBankingToken").type(JsonFieldType.STRING)
                            .description("openAPI 액세스 토큰").optional(),
                        fieldWithPath("depositAmount").type(JsonFieldType.NUMBER)
                            .description("입금 요청 금액"),
                        fieldWithPath("receiveClientName").type(JsonFieldType.STRING)
                            .description("입금 계좌 예금주명"),
                        fieldWithPath("bankAccountNumber").type(JsonFieldType.STRING)
                            .description("입금 계좌 번호"),
                        fieldWithPath("bankCode").type(JsonFieldType.STRING)
                            .description("입금 계좌 은행 코드")
                    ),
                    responseFields(
                        fieldWithPath("chargeAccountBankName").type(JsonFieldType.STRING)
                            .description("충전계좌 은행"),
                        fieldWithPath("chargeAccountNumber").type(JsonFieldType.STRING)
                            .description("충전계좌 번호"),
                        fieldWithPath("autoChargeAmount").type(JsonFieldType.NUMBER)
                            .description("자동충전 금액"),
                        fieldWithPath("feeAmount").type(JsonFieldType.NUMBER).description("수수료 금액"),
                        fieldWithPath("freeMonthlyFeeCount").type(JsonFieldType.NUMBER)
                            .description("남은 무료 수수료 횟수"),
                        fieldWithPath("balanceMoney").type(JsonFieldType.NUMBER)
                            .description("당근머니 잔액")
                    )
                ));
      }
    }
  }

}
