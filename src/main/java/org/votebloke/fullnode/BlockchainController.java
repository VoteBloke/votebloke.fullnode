package org.votebloke.fullnode;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.votebloke.blockchain.StringUtils;

import javax.print.attribute.standard.Media;
import java.util.ArrayList;
import java.util.Optional;

@RestController
@RequestMapping("/v1/blockchain")
public class BlockchainController {
  private final BlockchainModel chain = new BlockchainModel();

  @GetMapping("/accounts/create")
  public String createAccount() {
    chain.createAccount();
    return StringUtils.keyToString(chain.getAccount().getPublicKey());
  }

  @GetMapping("/transactions/unsigned")
  public ArrayList<TransactionGetBody> getUnsignedTransactions(@RequestParam Optional<String> keyId) {
    return chain.getUnsignedTransactions(keyId.orElse(null));
  }

  @GetMapping("/transactions/")
  @ResponseBody
  public ArrayList<TransactionGetBody> getAllTransactions() {
    return chain.getAllOutputTransactions();
  }

  @GetMapping("/transactions/elections")
  @ResponseBody
  public ArrayList<TransactionGetBody> getOpenElections() {
    return chain.getOpenElections();
  }

  @PostMapping(value = "/elections", consumes = MediaType.APPLICATION_JSON_VALUE)
  public String callElections(@RequestBody ElectionsPostBody body) {
    chain.callElections(body.elections, body.answers);
    return "Elections called";
  }

  @PostMapping(value = "/vote", consumes = MediaType.APPLICATION_JSON_VALUE)
  public String callElections(@RequestBody VotePostBody body) {
    chain.vote(body.answer, body.electionsTransactionId);
    return "Vote cast";
  }

  @PostMapping(value = "/tally", consumes = MediaType.APPLICATION_JSON_VALUE)
  public String tallyElections(@RequestBody TallyPostBody body) {
    chain.tallyElections(body.electionsTransactionId);
    return "Elections tallied";
  }
}
