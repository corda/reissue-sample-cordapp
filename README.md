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
Flow completed with result: C9228D73A4AF38CFB7D4DB768277B976456D0C63E8B2ECBD8FBBBDCC602344BC
</pre>

To list the issued coupon, run the following:
<pre>
<i>Alice's node:</i> flow start ListCandyCoupons holderParty: Alice, encumbered: null, couponRefs: null
</pre>
You should see output similar to the following one:
<pre>
Flow completed with result: [StateAndRef(state=TransactionState(data=50 TokenType(tokenIdentifier='CandyCoupon', fractionDigits=0) issued by CandyShop held by Alice, contract=com.r3.corda.lib.tokens.contracts.FungibleTokenContract, notary=O=Notary, L=London, C=GB, encumbrance=null, constraint=SignatureAttachmentConstraint(key=EC Public Key [5a:9f:70:fd:5f:d4:26:ed:55:66:42:78:a8:ee:09:ff:57:33:7e:e4]
            X: b4e2f8b9b8e4111622b2650de1acae5968c66fce005ca82a884d89c04e803d24
            Y: 4a2030c7d7614c23f72d2351d45f6fcf47b440c6e6255871206c5bd2e91c5adb
)), ref=C9228D73A4AF38CFB7D4DB768277B976456D0C63E8B2ECBD8FBBBDCC602344BC(0))]
</pre>
Note that the displayed reference consists of issuance transaction id.

Then you can check the issued coupon back-chain:
<pre>
<i>Alice's node:</i> flow start GetTransactionBackChain transactionId: C9228D73A4AF38CFB7D4DB768277B976456D0C63E8B2ECBD8FBBBDCC602344BC
</pre>
It should contain only issuance transaction id:
<pre>
Flow completed with result: [C9228D73A4AF38CFB7D4DB768277B976456D0C63E8B2ECBD8FBBBDCC602344BC]
</pre>

Let's say we want to give a coupon for 10 candies to Bob, but we only have a coupon for 50 candies. Run the following
to exchange the existing coupon to 5 coupons for 10 candies:
<pre>
<i>Alice's node:</i> flow start ExchangeCandyCoupons couponRefsStrings: [C9228D73A4AF38CFB7D4DB768277B976456D0C63E8B2ECBD8FBBBDCC602344BC(0)], newCouponCandies: [10, 10, 10, 10, 10] 
</pre>
The flow will return exchange transaction id: 
<pre>
Flow completed with result: 252790B276DABDD61A1AF036DF3617B4FECE6228B1A1100C6943D708E7EF528F
</pre>

List the available coupons again:
<pre>
<i>Alice's node:</i> flow start ListCandyCoupons holderParty: Alice, encumbered: null, couponRefs: null
</pre>
This time, you will see 5 coupons:
<pre>
Flow completed with result: [StateAndRef(state=TransactionState(data=10 TokenType(tokenIdentifier='CandyCoupon', fractionDigits=0) issued by CandyShop held by Alice, contract=com.r3.corda.lib.tokens.contracts.FungibleTokenContract, notary=O=Notary, L=London, C=GB, encumbrance=null, constraint=SignatureAttachmentConstraint(key=EC Public Key [5a:9f:70:fd:5f:d4:26:ed:55:66:42:78:a8:ee:09:ff:57:33:7e:e4]
            X: b4e2f8b9b8e4111622b2650de1acae5968c66fce005ca82a884d89c04e803d24
            Y: 4a2030c7d7614c23f72d2351d45f6fcf47b440c6e6255871206c5bd2e91c5adb
)), ref=252790B276DABDD61A1AF036DF3617B4FECE6228B1A1100C6943D708E7EF528F(0)), StateAndRef(state=TransactionState(data=10 TokenType(tokenIdentifier='CandyCoupon', fractionDigits=0) issued by CandyShop held by Alice, contract=com.r3.corda.lib.tokens.contracts.FungibleTokenContract, notary=O=Notary, L=London, C=GB, encumbrance=null, constraint=SignatureAttachmentConstraint(key=EC Public Key [5a:9f:70:fd:5f:d4:26:ed:55:66:42:78:a8:ee:09:ff:57:33:7e:e4]
            X: b4e2f8b9b8e4111622b2650de1acae5968c66fce005ca82a884d89c04e803d24
            Y: 4a2030c7d7614c23f72d2351d45f6fcf47b440c6e6255871206c5bd2e91c5adb
)), ref=252790B276DABDD61A1AF036DF3617B4FECE6228B1A1100C6943D708E7EF528F(1)), StateAndRef(state=TransactionState(data=10 TokenType(tokenIdentifier='CandyCoupon', fractionDigits=0) issued by CandyShop held by Alice, contract=com.r3.corda.lib.tokens.contracts.FungibleTokenContract, notary=O=Notary, L=London, C=GB, encumbrance=null, constraint=SignatureAttachmentConstraint(key=EC Public Key [5a:9f:70:fd:5f:d4:26:ed:55:66:42:78:a8:ee:09:ff:57:33:7e:e4]
            X: b4e2f8b9b8e4111622b2650de1acae5968c66fce005ca82a884d89c04e803d24
            Y: 4a2030c7d7614c23f72d2351d45f6fcf47b440c6e6255871206c5bd2e91c5adb
)), ref=252790B276DABDD61A1AF036DF3617B4FECE6228B1A1100C6943D708E7EF528F(2)), StateAndRef(state=TransactionState(data=10 TokenType(tokenIdentifier='CandyCoupon', fractionDigits=0) issued by CandyShop held by Alice, contract=com.r3.corda.lib.tokens.contracts.FungibleTokenContract, notary=O=Notary, L=London, C=GB, encumbrance=null, constraint=SignatureAttachmentConstraint(key=EC Public Key [5a:9f:70:fd:5f:d4:26:ed:55:66:42:78:a8:ee:09:ff:57:33:7e:e4]
            X: b4e2f8b9b8e4111622b2650de1acae5968c66fce005ca82a884d89c04e803d24
            Y: 4a2030c7d7614c23f72d2351d45f6fcf47b440c6e6255871206c5bd2e91c5adb
)), ref=252790B276DABDD61A1AF036DF3617B4FECE6228B1A1100C6943D708E7EF528F(3)), StateAndRef(state=TransactionState(data=10 TokenType(tokenIdentifier='CandyCoupon', fractionDigits=0) issued by CandyShop held by Alice, contract=com.r3.corda.lib.tokens.contracts.FungibleTokenContract, notary=O=Notary, L=London, C=GB, encumbrance=null, constraint=SignatureAttachmentConstraint(key=EC Public Key [5a:9f:70:fd:5f:d4:26:ed:55:66:42:78:a8:ee:09:ff:57:33:7e:e4]
            X: b4e2f8b9b8e4111622b2650de1acae5968c66fce005ca82a884d89c04e803d24
            Y: 4a2030c7d7614c23f72d2351d45f6fcf47b440c6e6255871206c5bd2e91c5adb
)), ref=252790B276DABDD61A1AF036DF3617B4FECE6228B1A1100C6943D708E7EF528F(4))]
</pre>

Now, you can give a coupon for 10 candies to Bob:
<pre>
<i>Alice's node:</i> flow start GiveCandyCoupons couponRefsStrings: [252790B276DABDD61A1AF036DF3617B4FECE6228B1A1100C6943D708E7EF528F(0)], newHolderParty: Bob
</pre>
The flow will return giveness transaction id: 
<pre>
Flow completed with result: 5270A4AA461F46B027CBE4F12EE80E310F554AE15CB830910647B6D42F2CCE9D
</pre>

Now, verify if the exchange transaction was added to the back-chain:
<pre>
<i>Alice's node:</i> flow start GetTransactionBackChain transactionId: 5270A4AA461F46B027CBE4F12EE80E310F554AE15CB830910647B6D42F2CCE9D
</pre>
Back-chain should contain 3 transaction ids now (issuance, exchange and giveness):
<pre>
Flow completed with result: [5270A4AA461F46B027CBE4F12EE80E310F554AE15CB830910647B6D42F2CCE9D, 252790B276DABDD61A1AF036DF3617B4FECE6228B1A1100C6943D708E7EF528F, C9228D73A4AF38CFB7D4DB768277B976456D0C63E8B2ECBD8FBBBDCC602344BC]</pre>

Then, transfer coupons between Alice and Bob a few times to make transaction back-chain longer (you will have to list
coupons to get their references). Remember you can transfer many copuons at once.

Next, list available tokens again:
<pre>
<i>Alice's node:</i> flow start ListCandyCoupons holderParty: Alice, encumbered: null
</pre>

Here is an output you could see:
<pre>
Flow completed with result: [StateAndRef(state=TransactionState(data=10 TokenType(tokenIdentifier='CandyCoupon', fractionDigits=0) issued by CandyShop held by Alice, contract=com.r3.corda.lib.tokens.contracts.FungibleTokenContract, notary=O=Notary, L=London, C=GB, encumbrance=null, constraint=SignatureAttachmentConstraint(key=EC Public Key [5a:9f:70:fd:5f:d4:26:ed:55:66:42:78:a8:ee:09:ff:57:33:7e:e4]
            X: b4e2f8b9b8e4111622b2650de1acae5968c66fce005ca82a884d89c04e803d24
            Y: 4a2030c7d7614c23f72d2351d45f6fcf47b440c6e6255871206c5bd2e91c5adb
)), ref=19D288C4FA174546C5A5D83DE36FF23FE974B8460426DD90988CC439080CA296(0)), StateAndRef(state=TransactionState(data=10 TokenType(tokenIdentifier='CandyCoupon', fractionDigits=0) issued by CandyShop held by Alice, contract=com.r3.corda.lib.tokens.contracts.FungibleTokenContract, notary=O=Notary, L=London, C=GB, encumbrance=null, constraint=SignatureAttachmentConstraint(key=EC Public Key [5a:9f:70:fd:5f:d4:26:ed:55:66:42:78:a8:ee:09:ff:57:33:7e:e4]
            X: b4e2f8b9b8e4111622b2650de1acae5968c66fce005ca82a884d89c04e803d24
            Y: 4a2030c7d7614c23f72d2351d45f6fcf47b440c6e6255871206c5bd2e91c5adb
)), ref=93DE1C7D7A056D404DD20EA0E750EF1547242CDB36F6ADFCA4543780D52B1FBF(0)), StateAndRef(state=TransactionState(data=10 TokenType(tokenIdentifier='CandyCoupon', fractionDigits=0) issued by CandyShop held by Alice, contract=com.r3.corda.lib.tokens.contracts.FungibleTokenContract, notary=O=Notary, L=London, C=GB, encumbrance=null, constraint=SignatureAttachmentConstraint(key=EC Public Key [5a:9f:70:fd:5f:d4:26:ed:55:66:42:78:a8:ee:09:ff:57:33:7e:e4]
            X: b4e2f8b9b8e4111622b2650de1acae5968c66fce005ca82a884d89c04e803d24
            Y: 4a2030c7d7614c23f72d2351d45f6fcf47b440c6e6255871206c5bd2e91c5adb
)), ref=93DE1C7D7A056D404DD20EA0E750EF1547242CDB36F6ADFCA4543780D52B1FBF(1))]
</pre>

Note that secure hashes (transaction ids) in state references might be the same or might differ. They are the same 
if they were generated in the same transaction and different otherwise. Now check back-chains of available coupons:
<pre>
<i>Alice's node:</i> flow start GetTransactionBackChain transactionId: 19D288C4FA174546C5A5D83DE36FF23FE974B8460426DD90988CC439080CA296
<i>Alice's node:</i> flow start GetTransactionBackChain transactionId: 93DE1C7D7A056D404DD20EA0E750EF1547242CDB36F6ADFCA4543780D52B1FBF
</pre>
One of the back-chains should be a subset of the other one: <!-- TODO: explain -->
<pre>
Flow completed with result: [19D288C4FA174546C5A5D83DE36FF23FE974B8460426DD90988CC439080CA296, FC2CF9CDC198FE003BFDCA5F35A0729D0C100D66DF139D501A08C07CDBB3AC25, 0C0E2BEA16428A8700253AC5DEE5DE8E95633F24E445E4F94C2B5480F75DFD8F, 5270A4AA461F46B027CBE4F12EE80E310F554AE15CB830910647B6D42F2CCE9D, 252790B276DABDD61A1AF036DF3617B4FECE6228B1A1100C6943D708E7EF528F, C9228D73A4AF38CFB7D4DB768277B976456D0C63E8B2ECBD8FBBBDCC602344BC]</pre>
<pre>
Flow completed with result: [93DE1C7D7A056D404DD20EA0E750EF1547242CDB36F6ADFCA4543780D52B1FBF, 0A7615D05E5D78266E10A026A0710F7983279867815C051F4F3C06B557B2917E, 19D288C4FA174546C5A5D83DE36FF23FE974B8460426DD90988CC439080CA296, FC2CF9CDC198FE003BFDCA5F35A0729D0C100D66DF139D501A08C07CDBB3AC25, 0C0E2BEA16428A8700253AC5DEE5DE8E95633F24E445E4F94C2B5480F75DFD8F, 5270A4AA461F46B027CBE4F12EE80E310F554AE15CB830910647B6D42F2CCE9D, 252790B276DABDD61A1AF036DF3617B4FECE6228B1A1100C6943D708E7EF528F, C9228D73A4AF38CFB7D4DB768277B976456D0C63E8B2ECBD8FBBBDCC602344BC]
</pre>

### Re-issuance use cases

#### Successful re-issuance

Someone might want to hide a fact that the coupon was transferred many times. To prune the back-chain, coupons
can be re-issued. Start with creating a re-issuance request:
<pre>
<i>Alice's node:</i> flow start RequestCandyCouponReIssuanceAndShareRequiredTransactions issuer: CandyShop, stateRefStringsToReIssue: [19D288C4FA174546C5A5D83DE36FF23FE974B8460426DD90988CC439080CA296(0), 93DE1C7D7A056D404DD20EA0E750EF1547242CDB36F6ADFCA4543780D52B1FBF(0), 93DE1C7D7A056D404DD20EA0E750EF1547242CDB36F6ADFCA4543780D52B1FBF(1)]
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
<i>Alice's node:</i> flow start ListCandyCoupons holderParty: Alice, encumbered: null
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
<i>Alice's node:</i> flow start ListCandyCoupons holderParty: Alice, encumbered: null
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
