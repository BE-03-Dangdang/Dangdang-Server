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
  String bankName = "????????????";
  String bankAccountNumber = "12354324534";

  @BeforeEach
  void setUp() {
    accessToken = jwtSetUp();
  }

  @Test
  @DisplayName("???????????? ?????? API ??????")
  void charge() throws Exception {
    PayRequest payRequest = new PayRequest(null, bankName, bankAccountNumber, 10000);
    String json = objectMapper.writeValueAsString(payRequest);
    PayResponse payResponse = new PayResponse("????????????", "234716230423", 20000, LocalDateTime.now());

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
                        .description("openAPI ????????? ??????").optional(),
                    fieldWithPath("bankName").type(JsonFieldType.STRING)
                        .description("?????? ?????? ?????????"),
                    fieldWithPath("bankAccountNumber").type(JsonFieldType.STRING)
                        .description("?????? ?????? ??????"),
                    fieldWithPath("amount").type(JsonFieldType.NUMBER).description("?????? ??????")
                ),
                responseFields(
                    fieldWithPath("bank").type(JsonFieldType.STRING).description("?????? ?????? ?????????"),
                    fieldWithPath("accountNumber").type(JsonFieldType.STRING)
                        .description("?????? ????????????"),
                    fieldWithPath("money").type(JsonFieldType.NUMBER).description("???????????? ??????"),
                    fieldWithPath("createdAt").type(JsonFieldType.STRING).description("????????????")
                )
            )
        );
  }

  @Test
  @DisplayName("???????????? ?????? API ??????")
  void withdraw() throws Exception {
    PayRequest payRequest = new PayRequest(null, bankName, bankAccountNumber, 10000);
    String json = objectMapper.writeValueAsString(payRequest);
    PayResponse payResponse = new PayResponse("????????????", "234716230423", 20000, LocalDateTime.now());

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
                        .description("openAPI ????????? ??????").optional(),
                    fieldWithPath("bankName").type(JsonFieldType.STRING)
                        .description("?????? ?????? ?????????"),
                    fieldWithPath("bankAccountNumber").type(JsonFieldType.STRING)
                        .description("?????? ?????? ??????"),
                    fieldWithPath("amount").type(JsonFieldType.NUMBER).description("?????? ??????")
                ),
                responseFields(
                    fieldWithPath("bank").type(JsonFieldType.STRING).description("?????? ?????? ?????????"),
                    fieldWithPath("accountNumber").type(JsonFieldType.STRING)
                        .description("?????? ????????????"),
                    fieldWithPath("money").type(JsonFieldType.NUMBER).description("???????????? ??????"),
                    fieldWithPath("createdAt").type(JsonFieldType.STRING).description("????????????")
                )
            )
        );
  }

  @Nested
  @DisplayName("???????????? ?????? API???")
  class Signup {

    @Nested
    @DisplayName("empty ?????? ????????? ?????? ?????? ?????? ????????????")
    class EmptyOrMinSize {

      @ParameterizedTest
      @EmptySource
      @DisplayName("BadRequest??? ????????????")
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
    @DisplayName("null ?????? ????????????")
    class Null {

      @ParameterizedTest
      @NullSource
      @DisplayName("BadRequest??? ????????????")
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
    @DisplayName("????????? ???????????? ????????????")
    class Success {

      @Test
      @DisplayName("payMemberId??? ????????????.")
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
  @DisplayName("?????? ?????? API???")
  class InquiryReceive {

    @Nested
    @DisplayName("???????????? ????????? 1??? ????????????")
    class DepositAmountSmallerThanMin {

      @Test
      @DisplayName("BadRequest??? ????????????")
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
    @DisplayName("null ?????? empty ?????? ????????????")
    class NullOrEmpty {

      @ParameterizedTest
      @NullAndEmptySource
      @DisplayName("BadRequest??? ????????????")
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
    @DisplayName("bankCode??? 3????????? ?????????")
    class BankCodeLengthError {

      @Test
      @DisplayName("BadRequest??? ????????????")
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
    @DisplayName("????????? ???????????? ????????????")
    class Success {

      @Test
      @DisplayName("?????? ?????? ????????? ????????????.")
      void successInquiryReceiveTest() throws Exception {
        ReceiveRequest receiveRequest = new ReceiveRequest(null, 10000, bankAccountNumber, "097");
        String json = objectMapper.writeValueAsString(receiveRequest);
        ReceiveResponse receiveResponse = new ReceiveResponse("?????????", false, "????????????", "3274623",
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
                            .description("openAPI ????????? ??????").optional(),
                        fieldWithPath("depositAmount").type(JsonFieldType.NUMBER)
                            .description("?????? ?????? ??????"),
                        fieldWithPath("bankAccountNumber").type(JsonFieldType.STRING)
                            .description("?????? ?????? ?????? ??????"),
                        fieldWithPath("bankName").type(JsonFieldType.STRING)
                            .description("?????? ?????? ?????? ?????????")
                    ),
                    responseFields(
                        fieldWithPath("receiveClientName").type(JsonFieldType.STRING)
                            .description("?????? ?????? ????????????"),
                        fieldWithPath("isMyAccount").type(JsonFieldType.BOOLEAN)
                            .description("?????? ?????? ?????? ??????"),
                        fieldWithPath("chargeAccountBankName").type(JsonFieldType.STRING)
                            .description("???????????? ??????"),
                        fieldWithPath("chargeAccountNumber").type(JsonFieldType.STRING)
                            .description("???????????? ??????"),
                        fieldWithPath("autoChargeAmount").type(JsonFieldType.NUMBER)
                            .description("???????????? ??????"),
                        fieldWithPath("feeAmount").type(JsonFieldType.NUMBER).description("????????? ??????"),
                        fieldWithPath("freeMonthlyFeeCount").type(JsonFieldType.NUMBER)
                            .description("?????? ?????? ????????? ??????")
                    )
                ));
      }
    }
  }

  @Nested
  @DisplayName("?????? API???")
  class Remittance {

    @Nested
    @DisplayName("????????? ???????????? ????????????")
    class Success {

      @Test
      @DisplayName("???????????? ????????? ????????????.")
      void successRemittanceTest() throws Exception {
        RemittanceRequest remittanceRequest = new RemittanceRequest(null, 10000, "?????????",
            bankAccountNumber, "????????????");
        String json = objectMapper.writeValueAsString(remittanceRequest);
        RemittanceResponse remittanceResponse = new RemittanceResponse("????????????", "3274623",
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
                            .description("openAPI ????????? ??????").optional(),
                        fieldWithPath("depositAmount").type(JsonFieldType.NUMBER)
                            .description("?????? ?????? ??????"),
                        fieldWithPath("receiveClientName").type(JsonFieldType.STRING)
                            .description("?????? ?????? ????????????"),
                        fieldWithPath("bankAccountNumber").type(JsonFieldType.STRING)
                            .description("?????? ?????? ??????"),
                        fieldWithPath("bankName").type(JsonFieldType.STRING)
                            .description("?????? ?????? ?????????")
                    ),
                    responseFields(
                        fieldWithPath("chargeAccountBankName").type(JsonFieldType.STRING)
                            .description("???????????? ??????"),
                        fieldWithPath("chargeAccountNumber").type(JsonFieldType.STRING)
                            .description("???????????? ??????"),
                        fieldWithPath("autoChargeAmount").type(JsonFieldType.NUMBER)
                            .description("???????????? ??????"),
                        fieldWithPath("feeAmount").type(JsonFieldType.NUMBER).description("????????? ??????"),
                        fieldWithPath("freeMonthlyFeeCount").type(JsonFieldType.NUMBER)
                            .description("?????? ?????? ????????? ??????"),
                        fieldWithPath("balanceMoney").type(JsonFieldType.NUMBER)
                            .description("???????????? ??????")
                    )
                ));
      }
    }
  }

}
