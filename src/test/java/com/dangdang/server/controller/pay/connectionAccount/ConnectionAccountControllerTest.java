package com.dangdang.server.controller.pay.connectionAccount;

import static com.dangdang.server.global.exception.ExceptionCode.BINDING_WRONG;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
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

import com.dangdang.server.controller.pay.TestHelper;
import com.dangdang.server.domain.pay.daangnpay.domain.connectionAccount.application.ConnectionAccountDatabaseService;
import com.dangdang.server.domain.pay.daangnpay.domain.connectionAccount.dto.AddConnectionAccountRequest;
import com.dangdang.server.domain.pay.daangnpay.domain.connectionAccount.dto.AllConnectionAccount;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import javax.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
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

@SpringBootTest
@AutoConfigureRestDocs
@AutoConfigureMockMvc
@Transactional
class ConnectionAccountControllerTest extends TestHelper {

  @MockBean
  ConnectionAccountDatabaseService connectionAccountDataBaseService;
  @Autowired
  MockMvc mockMvc;
  @Autowired
  ObjectMapper objectMapper;

  String accessToken;

  @BeforeEach
  void setUp() {
    accessToken = jwtSetUp();
  }

  @Test
  @DisplayName("전체 연결계좌 조회 API 성공")
  void getAllConnectionAccountResponseTest() throws Exception {
    AllConnectionAccount response1 = new AllConnectionAccount("신한은행",
        "348729184");
    AllConnectionAccount response2 = new AllConnectionAccount("전북은행",
        "04582637416");
    AllConnectionAccount response3 = new AllConnectionAccount("국민은행",
        "852648374234");
    List<AllConnectionAccount> allConnectionAccount = List.of(response1, response2,
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
                    fieldWithPath("allConnectionAccounts.[].bankName").type(
                        JsonFieldType.STRING).description("은행명"),
                    fieldWithPath("allConnectionAccounts.[].connectionAccountNumber").type(
                            JsonFieldType.STRING)
                        .description("계좌번호")
                )
            )
        );
  }

  @Nested
  @DisplayName("연결계좌 추가 API는")
  class AddConnectionAccountTest {

    @Nested
    @DisplayName("bankId에 null 값이 들어오면")
    class NullOrEmpty {

      @ParameterizedTest
      @NullSource
      @DisplayName("BadRequest를 응답한다")
      void failRequestTest(Long input) throws Exception {
        AddConnectionAccountRequest addConnectionAccountRequest = new AddConnectionAccountRequest(
            input);
        String json = objectMapper.writeValueAsString(addConnectionAccountRequest);
        String message = BINDING_WRONG.getMessage();

        mockMvc.perform(
                post("/connection-accounts")
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
      @DisplayName("연결계좌 추가에 성공하고 OK를 응답한다")
      void addConnectionAccountTest() throws Exception {
        AddConnectionAccountRequest addConnectionAccountRequest = new AddConnectionAccountRequest(
            1L);
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
                        fieldWithPath("bankAccountId").type(JsonFieldType.NUMBER)
                            .description("은행계좌 Id")
                    )
                )
            );
      }
    }
  }
}