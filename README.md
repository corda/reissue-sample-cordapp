<p align="center">	
  <img src="https://www.corda.net/wp-content/uploads/2016/11/fg005_corda_b.png" alt="Corda" width="500">	
</p>

# Re-issuance Sample CorDapp 
This CorDapp is re-issuance demo and demonstrates how to use re-issuance flows.
It also uses Ledger Graph to check transaction back-chain.

Re-issuance CorDapp: https://github.com/corda/reissue-cordapp

Ledger Graph CorDapp: https://github.com/corda/ledger-graph

## Sample CorDapp functionality
<!-- TODO: describe coupons and candies context -->

## Running the sample

### Deploy and run the nodes
Run the following commands:
<pre>
./gradlew deployNodes
./build/nodes/runnodes
</pre>
If you have any questions during setup, please go to https://docs.corda.net/getting-set-up.html for detailed setup 
instructions.

### Generate transaction back-chain
Once all nodes are started up (Notary, CandyShop, Alice & Bob), issue a candy coupon:
<pre>
<i>CandyShop's node:</i> flow start IssueCandyCoupons couponHolderParty: Alice, couponCandies: 50
</pre>
The flow will return issuance transaction id:
<pre>
Flow completed with result: E58E1A229240F57686ACBCDF83DBDFCF29BD39145DDDF4F6A3B046D721749B7C
</pre>

To list the issued coupon, run the following:
<pre>
<i>Alice's node:</i> flow start ListAvailableCandyCoupons holderParty: Alice, encumbered: null, couponRefs: null
</pre>
You should see output similar to the following one:
<pre>
Flow completed with result: [StateAndRef(state=TransactionState(data=50 TokenType(tokenIdentifier='DemoAppToken', fractionDigits=0) issued by CandyShop held by Alice, contract=com.r3.corda.lib.tokens.contracts.FungibleTokenContract, notary=O=Notary, L=London, C=GB, <b>encumbrance=null</b>, constraint=SignatureAttachmentConstraint(key=EC Public Key [5a:9f:70:fd:5f:d4:26:ed:55:66:42:78:a8:ee:09:ff:57:33:7e:e4]
            X: b4e2f8b9b8e4111622b2650de1acae5968c66fce005ca82a884d89c04e803d24
            Y: 4a2030c7d7614c23f72d2351d45f6fcf47b440c6e6255871206c5bd2e91c5adb
)), ref=9E7C3DC4F5C76FA9394BE6BA9AFDD724740D301BF8F1E2C8DAEC40930ED49885(0))]
</pre>
Note that the displayed reference is the same as issuance transaction id.

Now you can check issued token back-chain:
<pre>
<i>Alice's node:</i> flow start GetTransactionBackChain transactionId: 9E7C3DC4F5C76FA9394BE6BA9AFDD724740D301BF8F1E2C8DAEC40930ED49885
</pre>
It should contain only issuance transaction id:
<pre>
Flow completed with result: [9E7C3DC4F5C76FA9394BE6BA9AFDD724740D301BF8F1E2C8DAEC40930ED49885]
</pre>

Then, transfer 30 tokens to Bob:
<pre>
<i>Alice's node:</i> flow start MoveDemoAppTokens CandyShop: CandyShop, newTokenHolderParty: Bob, tokenAmount: 30 
</pre>
Note the returned transaction number is different this time: 
<pre>
Flow completed with result: 0CF1161EBE298EFC823663196CDE274D83116757505DC0634297671516677D95
</pre>

Now, verify if the update transaction was added to the back-chain:
<pre>
<i>Bob's node:</i> flow start GetTransactionBackChain transactionId: 0CF1161EBE298EFC823663196CDE274D83116757505DC0634297671516677D95
</pre>
Back-chain should contain 2 transaction ids now (issuance and update):
<pre>
Flow completed with result: [0CF1161EBE298EFC823663196CDE274D83116757505DC0634297671516677D95, 9E7C3DC4F5C76FA9394BE6BA9AFDD724740D301BF8F1E2C8DAEC40930ED49885]
</pre>

Then, transfer the tokens between Alice and Bob a few times to make transaction back-chain longer:
<pre>
<i>Bob's node:</i> flow start MoveDemoAppTokens CandyShop: CandyShop, newTokenHolderParty: Alice, tokenAmount: 20 
<i>Alice's node:</i> flow start MoveDemoAppTokens CandyShop: CandyShop, newTokenHolderParty: Bob, tokenAmount: 15
<i>Bob's node:</i> flow start MoveDemoAppTokens CandyShop: CandyShop, newTokenHolderParty: Alice, tokenAmount: 25
<i>Alice's node:</i> flow start MoveDemoAppTokens CandyShop: CandyShop, newTokenHolderParty: Bob, tokenAmount: 35
<i>Bob's node:</i> flow start MoveDemoAppTokens CandyShop: CandyShop, newTokenHolderParty: Alice, tokenAmount: 10
</pre>

Next, list available tokens again:
<pre>
<i>Alice's node:</i> flow start ListAvailableCandyCoupons holderParty: Alice, encumbered: null
</pre>
This time, you should see 2 tokens:
<pre>
Flow completed with result: [StateAndRef(state=TransactionState(data=15 TokenType(tokenIdentifier='DemoAppToken', fractionDigits=0) issued by CandyShop held by Alice, contract=com.r3.corda.lib.tokens.contracts.FungibleTokenContract, notary=O=Notary, L=London, C=GB, <b>encumbrance=null</b>, constraint=SignatureAttachmentConstraint(key=EC Public Key [5a:9f:70:fd:5f:d4:26:ed:55:66:42:78:a8:ee:09:ff:57:33:7e:e4]
            X: b4e2f8b9b8e4111622b2650de1acae5968c66fce005ca82a884d89c04e803d24
            Y: 4a2030c7d7614c23f72d2351d45f6fcf47b440c6e6255871206c5bd2e91c5adb
)), ref=3F3491D3FEE1704546A4C8878631D3C728E0C5B81D742F243813EC20E789C7CD(1)), StateAndRef(state=TransactionState(data=10 TokenType(tokenIdentifier='DemoAppToken', fractionDigits=0) issued by CandyShop held by Alice, contract=com.r3.corda.lib.tokens.contracts.FungibleTokenContract, notary=O=Notary, L=London, C=GB, <b>encumbrance=null</b>, constraint=SignatureAttachmentConstraint(key=EC Public Key [5a:9f:70:fd:5f:d4:26:ed:55:66:42:78:a8:ee:09:ff:57:33:7e:e4]
            X: b4e2f8b9b8e4111622b2650de1acae5968c66fce005ca82a884d89c04e803d24
            Y: 4a2030c7d7614c23f72d2351d45f6fcf47b440c6e6255871206c5bd2e91c5adb
)), ref=7C606CF78EC9A40081027618723464F0E09E7ECBA4BEF183F28F254E772FC489(0))]
</pre>

Now check back-chains of both of them:
<pre>
<i>Alice's node:</i> flow start GetTransactionBackChain transactionId: 3F3491D3FEE1704546A4C8878631D3C728E0C5B81D742F243813EC20E789C7CD
<i>Alice's node:</i> flow start GetTransactionBackChain transactionId: 7C606CF78EC9A40081027618723464F0E09E7ECBA4BEF183F28F254E772FC489
</pre>
The listed back-chains should be similar - one of them should be a subset of the other.
<pre>
Flow completed with result: [3F3491D3FEE1704546A4C8878631D3C728E0C5B81D742F243813EC20E789C7CD, 45FD8ED4AEFFEB6DED3CB19608EBB55D534DF686A5B1DCC7C528AF29AF359234, 32DBDD8CF3A84DFA55D04E67CA32518F2DFAAE8EA8C08DC77DF3459F14B5D6D4, 8E235472D5D5DB6D83AF9FE05A8C516FF0E86E4180B8207226DD9464C8BD542F, 0CF1161EBE298EFC823663196CDE274D83116757505DC0634297671516677D95, 9E7C3DC4F5C76FA9394BE6BA9AFDD724740D301BF8F1E2C8DAEC40930ED49885]
</pre>
<pre>
Flow completed with result: [7C606CF78EC9A40081027618723464F0E09E7ECBA4BEF183F28F254E772FC489, 3F3491D3FEE1704546A4C8878631D3C728E0C5B81D742F243813EC20E789C7CD, 45FD8ED4AEFFEB6DED3CB19608EBB55D534DF686A5B1DCC7C528AF29AF359234, 32DBDD8CF3A84DFA55D04E67CA32518F2DFAAE8EA8C08DC77DF3459F14B5D6D4, 8E235472D5D5DB6D83AF9FE05A8C516FF0E86E4180B8207226DD9464C8BD542F, 0CF1161EBE298EFC823663196CDE274D83116757505DC0634297671516677D95, 9E7C3DC4F5C76FA9394BE6BA9AFDD724740D301BF8F1E2C8DAEC40930ED49885]
</pre>

### Re-issuance use cases

#### Successful re-issuance

To prune the back-chain, tokens can be re-issued. Start with creating a re-issuance request:
<pre>
<i>Alice's node:</i> flow start RequestDemoAppTokensReIssuanceAndShareRequiredTransactions CandyShop: CandyShop, stateRefStringsToReIssue: [3F3491D3FEE1704546A4C8878631D3C728E0C5B81D742F243813EC20E789C7CD(1), 7C606CF78EC9A40081027618723464F0E09E7ECBA4BEF183F28F254E772FC489(0)]
</pre>
The flow will return re-issuance request transaction id:
<pre>
Flow completed with result: 57DE34F7938E68DDAA51DADE2B189EDB8F6CACA706D4AA3ED19B6BC5D6C9A315
</pre>

Then CandyShop has 2 options: accept the request or reject it. We will focus on acceptance as this use case describes 
successful re-issuance. Run the following command to list all re-issuance requests:
<pre>
<i>CandyShop's node:</i> run vaultQuery contractStateType: com.r3.corda.lib.reissuance.states.ReIssuanceRequest
</pre>

There should be exactly one re-issuance request, and the output of the above command should be similar to the following:
<pre>
states:
- state:
    data: !<com.r3.corda.lib.reissuance.states.ReIssuanceRequest>
      CandyShop: "O=CandyShop, L=London, C=GB"
      requester: "O=Alice, L=New York, C=US"
      stateRefsToReIssue:
      - txhash: "3F3491D3FEE1704546A4C8878631D3C728E0C5B81D742F243813EC20E789C7CD"
        index: 1
      - txhash: "7C606CF78EC9A40081027618723464F0E09E7ECBA4BEF183F28F254E772FC489"
        index: 0
      assetIssuanceCommand:
        token:
          CandyShop: "O=CandyShop, L=London, C=GB"
          tokenType:
            tokenIdentifier: "CandyCoupon"
            fractionDigits: 0
        outputs:
        - 0
        - 1
      assetIssuanceSigners:
      - "O=CandyShop, L=London, C=GB"
      - "O=CandyShop, L=London, C=GB"
    contract: "com.r3.corda.lib.reissuance.contracts.ReIssuanceRequestContract"
    notary: "O=Notary, L=London, C=GB"
    encumbrance: null
    constraint: !<net.corda.core.contracts.SignatureAttachmentConstraint>
      key: "aSq9DsNNvGhYxYyqA9wd2eduEAZ5AXWgJTbTEw3G5d2maAq8vtLE4kZHgCs5jcB1N31cx1hpsLeqG2ngSysVHqcXhbNts6SkRWDaV7xNcr6MtcbufGUchxredBb6"
  ref:
    txhash: "57DE34F7938E68DDAA51DADE2B189EDB8F6CACA706D4AA3ED19B6BC5D6C9A315"
    index: 0
statesMetadata:
- ref:
    txhash: "57DE34F7938E68DDAA51DADE2B189EDB8F6CACA706D4AA3ED19B6BC5D6C9A315"
    index: 0
  contractStateClassName: "com.r3.corda.lib.reissuance.states.ReIssuanceRequest"
  recordedTime: "2020-10-09T16:32:36.114Z"
  consumedTime: null
  status: "UNCONSUMED"
  notary: "O=Notary, L=London, C=GB"
  lockId: null
  lockUpdateTime: null
  relevancyStatus: "RELEVANT"
  constraintInfo:
    constraint:
      key: "aSq9DsNNvGhYxYyqA9wd2eduEAZ5AXWgJTbTEw3G5d2maAq8vtLE4kZHgCs5jcB1N31cx1hpsLeqG2ngSysVHqcXhbNts6SkRWDaV7xNcr6MtcbufGUchxredBb6"
totalStatesAvailable: -1
stateTypes: "UNCONSUMED"
otherResults: []
</pre>

To accept the request - re-issue locked state and create re-issuance lock, run:
<pre>
<i>CandyShop's node:</i> flow start ReIssueDemoAppTokens reIssuanceRequestRefString: 57DE34F7938E68DDAA51DADE2B189EDB8F6CACA706D4AA3ED19B6BC5D6C9A315(0)
</pre>
The flow will return re-issuance transaction id:
<pre>
Flow completed with result: A8D051774085189C504708B751EF72D55BB09DAB7653E524414CF3005F3C8C04
</pre>

Now, list available tokens to make sure new tokens had been re-issued before exiting original states from the vault:
<pre>
<i>Alice's node:</i> flow start ListAvailableCandyCoupons holderParty: Alice, encumbered: null
</pre>

You should see both original states and their duplicates. Note that original states are unencumbered, and 
the duplicated states are encumbered.
<pre>
Flow completed with result: [StateAndRef(state=TransactionState(data=15 TokenType(tokenIdentifier='DemoAppToken', fractionDigits=0) issued by CandyShop held by Alice, contract=com.r3.corda.lib.tokens.contracts.FungibleTokenContract, notary=O=Notary, L=London, C=GB, <b>encumbrance=null</b>, constraint=SignatureAttachmentConstraint(key=EC Public Key [5a:9f:70:fd:5f:d4:26:ed:55:66:42:78:a8:ee:09:ff:57:33:7e:e4]
            X: b4e2f8b9b8e4111622b2650de1acae5968c66fce005ca82a884d89c04e803d24
            Y: 4a2030c7d7614c23f72d2351d45f6fcf47b440c6e6255871206c5bd2e91c5adb
)), ref=3F3491D3FEE1704546A4C8878631D3C728E0C5B81D742F243813EC20E789C7CD(1)), StateAndRef(state=TransactionState(data=10 TokenType(tokenIdentifier='DemoAppToken', fractionDigits=0) issued by CandyShop held by Alice, contract=com.r3.corda.lib.tokens.contracts.FungibleTokenContract, notary=O=Notary, L=London, C=GB, <b>encumbrance=null</b>, constraint=SignatureAttachmentConstraint(key=EC Public Key [5a:9f:70:fd:5f:d4:26:ed:55:66:42:78:a8:ee:09:ff:57:33:7e:e4]
            X: b4e2f8b9b8e4111622b2650de1acae5968c66fce005ca82a884d89c04e803d24
            Y: 4a2030c7d7614c23f72d2351d45f6fcf47b440c6e6255871206c5bd2e91c5adb
)), ref=7C606CF78EC9A40081027618723464F0E09E7ECBA4BEF183F28F254E772FC489(0)), StateAndRef(state=TransactionState(data=15 TokenType(tokenIdentifier='DemoAppToken', fractionDigits=0) issued by CandyShop held by Alice, contract=com.r3.corda.lib.tokens.contracts.FungibleTokenContract, notary=O=Notary, L=London, C=GB, <b>encumbrance=1</b>, constraint=SignatureAttachmentConstraint(key=EC Public Key [5a:9f:70:fd:5f:d4:26:ed:55:66:42:78:a8:ee:09:ff:57:33:7e:e4]
            X: b4e2f8b9b8e4111622b2650de1acae5968c66fce005ca82a884d89c04e803d24
            Y: 4a2030c7d7614c23f72d2351d45f6fcf47b440c6e6255871206c5bd2e91c5adb
)), ref=A8D051774085189C504708B751EF72D55BB09DAB7653E524414CF3005F3C8C04(0)), StateAndRef(state=TransactionState(data=10 TokenType(tokenIdentifier='DemoAppToken', fractionDigits=0) issued by CandyShop held by Alice, contract=com.r3.corda.lib.tokens.contracts.FungibleTokenContract, notary=O=Notary, L=London, C=GB, <b>encumbrance=2</b>, constraint=SignatureAttachmentConstraint(key=EC Public Key [5a:9f:70:fd:5f:d4:26:ed:55:66:42:78:a8:ee:09:ff:57:33:7e:e4]
            X: b4e2f8b9b8e4111622b2650de1acae5968c66fce005ca82a884d89c04e803d24
            Y: 4a2030c7d7614c23f72d2351d45f6fcf47b440c6e6255871206c5bd2e91c5adb
)), ref=A8D051774085189C504708B751EF72D55BB09DAB7653E524414CF3005F3C8C04(1))]
</pre>

Newly generated states, even though they are available, can't be spent without re-issuance lock.
Run the following command to list all re-issuance locks:
<pre>
<i>Alice's node:</i> run vaultQuery contractStateType: com.r3.corda.lib.reissuance.states.ReIssuanceLock
</pre>
There should be exactly one re-issuance lock available: 
<pre>
states:
- state:
    data: !<com.r3.corda.lib.reissuance.states.ReIssuanceLock>
      CandyShop: "O=CandyShop, L=London, C=GB"
      requester: "O=Alice, L=New York, C=US"
      originalStates:
      - state:
          data: !<com.r3.corda.lib.tokens.contracts.states.FungibleToken>
            amount: "15 TokenType(tokenIdentifier='DemoAppToken', fractionDigits=0)\
              \ issued by CandyShop"
            holder: "O=Alice, L=New York, C=US"
            tokenTypeJarHash: null
          contract: "com.r3.corda.lib.tokens.contracts.FungibleTokenContract"
          notary: "O=Notary, L=London, C=GB"
          encumbrance: null
          constraint: !<net.corda.core.contracts.SignatureAttachmentConstraint>
            key: "aSq9DsNNvGhYxYyqA9wd2eduEAZ5AXWgJTbTJUgKok6g1qtLzJhoTKPioLoBfSTZU2Eg4sCfe9nn1urJJdE2r1pGVVVTDWJqs3EEb4n9tsXDwyYUYHMPT4XqgkKc"
        ref:
          txhash: "3F3491D3FEE1704546A4C8878631D3C728E0C5B81D742F243813EC20E789C7CD"
          index: 1
      - state:
          data: !<com.r3.corda.lib.tokens.contracts.states.FungibleToken>
            amount: "10 TokenType(tokenIdentifier='DemoAppToken', fractionDigits=0)\
              \ issued by CandyShop"
            holder: "O=Alice, L=New York, C=US"
            tokenTypeJarHash: null
          contract: "com.r3.corda.lib.tokens.contracts.FungibleTokenContract"
          notary: "O=Notary, L=London, C=GB"
          encumbrance: null
          constraint: !<net.corda.core.contracts.SignatureAttachmentConstraint>
            key: "aSq9DsNNvGhYxYyqA9wd2eduEAZ5AXWgJTbTJUgKok6g1qtLzJhoTKPioLoBfSTZU2Eg4sCfe9nn1urJJdE2r1pGVVVTDWJqs3EEb4n9tsXDwyYUYHMPT4XqgkKc"
        ref:
          txhash: "7C606CF78EC9A40081027618723464F0E09E7ECBA4BEF183F28F254E772FC489"
          index: 0
      status: "ACTIVE"
      CandyShopIsRequiredExitTransactionSigner: true
    contract: "com.r3.corda.lib.reissuance.contracts.ReIssuanceLockContract"
    notary: "O=Notary, L=London, C=GB"
    encumbrance: 0
    constraint: !<net.corda.core.contracts.SignatureAttachmentConstraint>
      key: "aSq9DsNNvGhYxYyqA9wd2eduEAZ5AXWgJTbTEw3G5d2maAq8vtLE4kZHgCs5jcB1N31cx1hpsLeqG2ngSysVHqcXhbNts6SkRWDaV7xNcr6MtcbufGUchxredBb6"
  ref:
    txhash: "A8D051774085189C504708B751EF72D55BB09DAB7653E524414CF3005F3C8C04"
    index: 2
statesMetadata:
- ref:
    txhash: "A8D051774085189C504708B751EF72D55BB09DAB7653E524414CF3005F3C8C04"
    index: 2
  contractStateClassName: "com.r3.corda.lib.reissuance.states.ReIssuanceLock"
  recordedTime: "2020-10-09T16:33:55.450Z"
  consumedTime: null
  status: "UNCONSUMED"
  notary: "O=Notary, L=London, C=GB"
  lockId: null
  lockUpdateTime: null
  relevancyStatus: "RELEVANT"
  constraintInfo:
    constraint:
      key: "aSq9DsNNvGhYxYyqA9wd2eduEAZ5AXWgJTbTEw3G5d2maAq8vtLE4kZHgCs5jcB1N31cx1hpsLeqG2ngSysVHqcXhbNts6SkRWDaV7xNcr6MtcbufGUchxredBb6"
totalStatesAvailable: -1
stateTypes: "UNCONSUMED"
otherResults: []
</pre>

Now it's time to exit the original tokens from the vault:
<pre>
<i>Alice's node:</i> flow start RedeemDemoAppTokens CandyShop: CandyShop, encumbered: null, tokensNum: null, tokenRefsStrings: [3F3491D3FEE1704546A4C8878631D3C728E0C5B81D742F243813EC20E789C7CD(1), 7C606CF78EC9A40081027618723464F0E09E7ECBA4BEF183F28F254E772FC489(0)]
</pre>
The flow will return id of the exit transaction:
<pre>
Flow completed with result: CC306740156BB9442A58FFE9AC5732A64F822568430854F5FF69430769BC6EB5
</pre>

Next, upload exit transaction as an attachment providing its id:
<pre>
<i>Alice's node:</i> flow start UploadTransactionAsAttachment transactionId: CC306740156BB9442A58FFE9AC5732A64F822568430854F5FF69430769BC6EB5
</pre>
The flow will return newly created attachment id:
<pre>
Flow completed with result: E0F43A1509F4F2402590AE383E36D42A946FDEF23BB0863C1953130CC194AAA7
</pre>

Then you can unlock re-issued states, providing their references, re-issuance lock reference, and attachment id of 
token exit transaction:
<pre>
<i>Alice's node:</i> flow start UnlockReIssuedDemoAppStates reIssuedStatesRefStrings: [A8D051774085189C504708B751EF72D55BB09DAB7653E524414CF3005F3C8C04(0), A8D051774085189C504708B751EF72D55BB09DAB7653E524414CF3005F3C8C04(1)], reIssuanceLockRefString: A8D051774085189C504708B751EF72D55BB09DAB7653E524414CF3005F3C8C04(2), deletedStateTransactionHashes: [E0F43A1509F4F2402590AE383E36D42A946FDEF23BB0863C1953130CC194AAA7]
</pre>
The flows will return the id of unlock re-issued states transaction:
<pre>
Flow completed with result: CB753D19959E8858B52CB79AF1930478054C2890C51174AAE45EA500A96AFECE
</pre>

Now list tokens one more time:
<pre>
<i>Alice's node:</i> flow start ListAvailableCandyCoupons holderParty: Alice, encumbered: null
</pre>
Note that the re-issued states are now unencumbered:
<pre>
Flow completed with result: [StateAndRef(state=TransactionState(data=15 TokenType(tokenIdentifier='DemoAppToken', fractionDigits=0) issued by CandyShop held by Alice, contract=com.r3.corda.lib.tokens.contracts.FungibleTokenContract, notary=O=Notary, L=London, C=GB, <b>encumbrance=null</b>, constraint=SignatureAttachmentConstraint(key=EC Public Key [5a:9f:70:fd:5f:d4:26:ed:55:66:42:78:a8:ee:09:ff:57:33:7e:e4]
            X: b4e2f8b9b8e4111622b2650de1acae5968c66fce005ca82a884d89c04e803d24
            Y: 4a2030c7d7614c23f72d2351d45f6fcf47b440c6e6255871206c5bd2e91c5adb
)), ref=CB753D19959E8858B52CB79AF1930478054C2890C51174AAE45EA500A96AFECE(0)), StateAndRef(state=TransactionState(data=10 TokenType(tokenIdentifier='DemoAppToken', fractionDigits=0) issued by CandyShop held by Alice, contract=com.r3.corda.lib.tokens.contracts.FungibleTokenContract, notary=O=Notary, L=London, C=GB, <b>encumbrance=null</b>, constraint=SignatureAttachmentConstraint(key=EC Public Key [5a:9f:70:fd:5f:d4:26:ed:55:66:42:78:a8:ee:09:ff:57:33:7e:e4]
            X: b4e2f8b9b8e4111622b2650de1acae5968c66fce005ca82a884d89c04e803d24
            Y: 4a2030c7d7614c23f72d2351d45f6fcf47b440c6e6255871206c5bd2e91c5adb
)), ref=CB753D19959E8858B52CB79AF1930478054C2890C51174AAE45EA500A96AFECE(1))]
</pre>

Then, list back-chain of the re-issued states:
<pre>
<i>Alice's node:</i> flow start GetTransactionBackChain transactionId: CB753D19959E8858B52CB79AF1930478054C2890C51174AAE45EA500A96AFECE
</pre>
The result will contain identifiers of 3 transactions:
- requesting re-issuance
- re-issuance
- unlocking re-issued tokens
<pre>
Flow completed with result: [CB753D19959E8858B52CB79AF1930478054C2890C51174AAE45EA500A96AFECE, A8D051774085189C504708B751EF72D55BB09DAB7653E524414CF3005F3C8C04, 57DE34F7938E68DDAA51DADE2B189EDB8F6CACA706D4AA3ED19B6BC5D6C9A315]
</pre>

<!-- consider showing that encumbered tokens can't be spent and after they are unencumbered - they can -->

#### Rejected re-issuance request
As mentioned before, CandyShop doesn't have to accept re-issuance request. To reject it, they should run:
<pre>
<i>CandyShop's node:</i> flow start RejectDemoAppTokensReIssuanceRequest reIssuanceRequestRefString: 57DE34F7938E68DDAA51DADE2B189EDB8F6CACA706D4AA3ED19B6BC5D6C9A315(0)
</pre>

#### Original tokens have been consumed, and it's impossible to unlock re-issued states
If at least one of tokens to be re-issued have been consumed (lets say Alice transferred 1 token to Bob) 
after their locked copies have been created, all re-issued tokens are useless - they will never be unlocked. 
It that case, re-issued tokens and corresponding re-issuance lock can be exited from the ledger:
<pre>
<i>Alice's node:</i> 
flow start DeleteReIssuedDemoAppStatesAndLock reIssuedStatesRefStrings: [A8D051774085189C504708B751EF72D55BB09DAB7653E524414CF3005F3C8C04(0), A8D051774085189C504708B751EF72D55BB09DAB7653E524414CF3005F3C8C04(1)], reIssuanceLockRefString: A8D051774085189C504708B751EF72D55BB09DAB7653E524414CF3005F3C8C04(2)
</pre>
