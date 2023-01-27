package com.dangdang.server.controller.pay.connectionAccount;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.dangdang.server.domain.member.application.MemberService;
import com.dangdang.server.domain.member.domain.RedisAuthCodeRepository;
import com.dangdang.server.domain.member.domain.entity.RedisAuthCode;
import com.dangdang.server.domain.member.dto.request.MemberSignUpRequest;
import com.dangdang.server.domain.member.dto.response.MemberCertifyResponse;
import com.dangdang.server.domain.pay.daangnpay.domain.connectionAccount.application.ConnectionAccountDatabaseService;
import com.dangdang.server.domain.pay.daangnpay.domain.connectionAccount.dto.AddConnectionAccountRequest;
import com.dangdang.server.domain.pay.daangnpay.domain.connectionAccount.dto.GetAllConnectionAccountResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
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

@SpringBootTest
@AutoConfigureRestDocs
@AutoConfigureMockMvc
@Transactional
class ConnectionAccountDatabaseControllerTest {

  @MockBean
  ConnectionAccountDatabaseService connectionAccountDataBaseService;
  @Autowired
  RedisAuthCodeRepository redisAuthCodeRepository;
  @Autowired
  MemberService memberService;
  @Autowired
  MockMvc mockMvc;
  @Autowired
  ObjectMapper objectMapper;

  String accessToken;

  @BeforeEach
  void setUp() {
    // token
    String phoneNumber = "1";
    MemberSignUpRequest memberSignUpRequest = new MemberSignUpRequest("천호동", phoneNumber, "url",
        "닉네임");
    redisAuthCodeRepository.save(new RedisAuthCode(phoneNumber));

    MemberCertifyResponse signup = memberService.signup(memberSignUpRequest);
    accessToken = "Bearer " + signup.getAccessToken();
  }

  @Test
  @DisplayName("연결계좌 추가 API 성공")
  void addConnectionAccountTest() throws Exception {
    AddConnectionAccountRequest addConnectionAccountRequest = new AddConnectionAccountRequest(1L);
    String json = objectMapper.writeValueAsString(addConnectionAccountRequest);

    mockMvc.perform(
            post("/connection-accounts")
                .header("AccessToken", accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(json)
        )
        .andExpect(status().isOk())
        .andDo(
            document(
                "ConnectionAccountDatabaseController/addConnectionAccount",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestHeaders(
                    headerWithName("AccessToken").description("jwt header")
                ),
                requestFields(
                    fieldWithPath("bankAccountId").type(JsonFieldType.NUMBER).description("은행계좌 Id")
                )
            )
        );
  }

  @Test
  @DisplayName("전체 연결계좌 조회 API 성공")
  void getAllConnectionAccountResponseTest() throws Exception {
    GetAllConnectionAccountResponse response1 = new GetAllConnectionAccountResponse("신한은행",
        "348729184");
    GetAllConnectionAccountResponse response2 = new GetAllConnectionAccountResponse("전북은행",
        "04582637416");
    GetAllConnectionAccountResponse response3 = new GetAllConnectionAccountResponse("국민은행",
        "852648374234");
    List<GetAllConnectionAccountResponse> allConnectionAccount = List.of(response1, response2,
        response3);

    doReturn(allConnectionAccount).when(connectionAccountDataBaseService)
        .getAllConnectionAccount(any());

    mockMvc.perform(
            get("/connection-accounts")
                .header("AccessToken", accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
        )
        .andExpect(status().isOk())
        .andDo(
            document(
                "ConnectionAccountDatabaseController/getAllConnectionAccountResponse",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestHeaders(
                    headerWithName("AccessToken").description("jwt header")
                ),
                responseFields(
                    fieldWithPath("[].bankName").type(
                        JsonFieldType.STRING).description("은행명"),
                    fieldWithPath("[].connectionAccountNumber").type(
                            JsonFieldType.STRING)
                        .description("계좌번호")
                )
            )
        );
  }
}