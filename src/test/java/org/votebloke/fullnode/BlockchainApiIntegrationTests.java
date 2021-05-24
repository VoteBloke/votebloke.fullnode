package org.votebloke.fullnode;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.apache.tomcat.util.codec.binary.Base64;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.votebloke.blockchain.Account;
import org.votebloke.blockchain.StringUtils;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Integration tests of the application. */
@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BlockchainApiIntegrationTests {

  @Autowired MockMvc mockMvc;

  KeyPair keyPair;
  String testPublicKey;

  @Autowired ObjectMapper mapper;

  @BeforeEach
  void setUp() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException {
    keyPair = Account.generateKeys();
    testPublicKey = StringUtils.keyToString(keyPair.getPublic());
  }

  @Test
  @Order(0)
  void createAccountReturnsBase64PublicKeyFromHeader() throws Exception {
    this.mockMvc
        .perform(get("/v1/blockchain/accounts/create").header("public-key", testPublicKey))
        .andExpect(status().isOk())
        .andExpect(content().json("{'public-key':'" + testPublicKey + "'}"));
  }

  @Test
  @Order(1)
  void getUnsignedTransactionsEmptyOnEmptyChain() throws Exception {
    this.mockMvc
        .perform(get("/v1/blockchain/transactions/unsigned"))
        .andExpect(status().isOk())
        .andExpect(content().string("[]"));
  }

  @Test
  @Order(2)
  void getTransactionsEmptyOnEmptyChain() throws Exception {
    this.mockMvc
        .perform(get("/v1/blockchain/transactions"))
        .andExpect(status().isOk())
        .andExpect(content().string("[]"));
  }

  @Test
  @Order(3)
  void createElectionsOkOnEmptyChain() throws Exception {
    String payload =
        mapper.writeValueAsString(
            new ElectionsPostBody("test question", new String[] {"answer1", "answer2"}));

    this.mockMvc
        .perform(
            post("/v1/blockchain/elections")
                .header("public-key", testPublicKey)
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
        .andExpect(status().isOk());
  }

  @Test
  @Order(4)
  void unsignedElectionsAfterPostingElections() throws Exception {
    this.mockMvc
        .perform(
            get("/v1/blockchain/transactions/unsigned").accept(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].transactionId").hasJsonPath())
        .andExpect(jsonPath("$[0].timeStamp").hasJsonPath())
        .andExpect(jsonPath("$[0].entryType").hasJsonPath())
        .andExpect(jsonPath("$[0].transactionAuthor").hasJsonPath())
        .andExpect(jsonPath("$[0].entryMetadata").hasJsonPath())
        .andExpect(jsonPath("$[0].dataToSign").hasJsonPath());
  }

  @Test
  @Order(5)
  void signedTransactionsEmptyAfterPostingElections() throws Exception {
    this.mockMvc
        .perform(get("/v1/blockchain/transactions").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().string("[]"));
  }

  @Test
  @Order(6)
  void signingElectionsReturnsStatusOk() throws Exception {
    String response =
        this.mockMvc
            .perform(
                get("/v1/blockchain/transactions/unsigned")
                    .accept(MediaType.APPLICATION_JSON_VALUE))
            .andReturn()
            .getResponse()
            .getContentAsString();

    String dataToEncrypt = JsonPath.parse(response).read("$[0].dataToSign", String.class);
    String transactionId = JsonPath.parse(response).read("$[0].transactionId", String.class);
    String encryptedData =
        new String(
            Base64.encodeBase64(StringUtils.signWithEcdsa(keyPair.getPrivate(), dataToEncrypt)));

    String payload = mapper.writeValueAsString(new SignPostBody(transactionId, encryptedData));
    this.mockMvc
        .perform(
            post("/v1/blockchain/transactions/sign")
                .header("public-key", testPublicKey)
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
        .andExpect(status().isOk());
  }

  @Test
  @Order(7)
  void unsignedTransactionsEmptyAfterSigningAllTransactions() throws Exception {
    this.mockMvc
        .perform(
            get("/v1/blockchain/transactions/unsigned").accept(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isOk())
        .andExpect(content().string("[]"));
  }
}
