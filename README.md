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
Flow completed with result: 7CFE1FFB9FFFDC2C904F32A8CF7836A1388E762F755177D37729805B58EB7EAC
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
)), ref=7CFE1FFB9FFFDC2C904F32A8CF7836A1388E762F755177D37729805B58EB7EAC(0))]
</pre>
Note that the displayed reference consists of issuance transaction id.

Then you can check the issued coupon back-chain:
<pre>
<i>Alice's node:</i> flow start GetTransactionBackChain transactionId: 7CFE1FFB9FFFDC2C904F32A8CF7836A1388E762F755177D37729805B58EB7EAC
</pre>
It should contain only issuance transaction id:
<pre>
Flow completed with result: [7CFE1FFB9FFFDC2C904F32A8CF7836A1388E762F755177D37729805B58EB7EAC]
</pre>

Let's say we want to give a coupon for 10 candies to Bob, but we only have a coupon for 50 candies. Run the following
to exchange the existing coupon to 5 coupons for 10 candies:
<pre>
<i>Alice's node:</i> flow start ExchangeCandyCoupons couponRefsStrings: [7CFE1FFB9FFFDC2C904F32A8CF7836A1388E762F755177D37729805B58EB7EAC(0)], newCouponCandies: [10, 10, 10, 10, 10] 
</pre>
The flow will return exchange transaction id: 
<pre>
Flow completed with result: 02F5848E8EDF0468CC2BFCFD0C8F81FC10CE729D6CFA95D660FF14D72C24F648
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
)), ref=02F5848E8EDF0468CC2BFCFD0C8F81FC10CE729D6CFA95D660FF14D72C24F648(0)), StateAndRef(state=TransactionState(data=10 TokenType(tokenIdentifier='CandyCoupon', fractionDigits=0) issued by CandyShop held by Alice, contract=com.r3.corda.lib.tokens.contracts.FungibleTokenContract, notary=O=Notary, L=London, C=GB, encumbrance=null, constraint=SignatureAttachmentConstraint(key=EC Public Key [5a:9f:70:fd:5f:d4:26:ed:55:66:42:78:a8:ee:09:ff:57:33:7e:e4]
            X: b4e2f8b9b8e4111622b2650de1acae5968c66fce005ca82a884d89c04e803d24
            Y: 4a2030c7d7614c23f72d2351d45f6fcf47b440c6e6255871206c5bd2e91c5adb
)), ref=02F5848E8EDF0468CC2BFCFD0C8F81FC10CE729D6CFA95D660FF14D72C24F648(1)), StateAndRef(state=TransactionState(data=10 TokenType(tokenIdentifier='CandyCoupon', fractionDigits=0) issued by CandyShop held by Alice, contract=com.r3.corda.lib.tokens.contracts.FungibleTokenContract, notary=O=Notary, L=London, C=GB, encumbrance=null, constraint=SignatureAttachmentConstraint(key=EC Public Key [5a:9f:70:fd:5f:d4:26:ed:55:66:42:78:a8:ee:09:ff:57:33:7e:e4]
            X: b4e2f8b9b8e4111622b2650de1acae5968c66fce005ca82a884d89c04e803d24
            Y: 4a2030c7d7614c23f72d2351d45f6fcf47b440c6e6255871206c5bd2e91c5adb
)), ref=02F5848E8EDF0468CC2BFCFD0C8F81FC10CE729D6CFA95D660FF14D72C24F648(2)), StateAndRef(state=TransactionState(data=10 TokenType(tokenIdentifier='CandyCoupon', fractionDigits=0) issued by CandyShop held by Alice, contract=com.r3.corda.lib.tokens.contracts.FungibleTokenContract, notary=O=Notary, L=London, C=GB, encumbrance=null, constraint=SignatureAttachmentConstraint(key=EC Public Key [5a:9f:70:fd:5f:d4:26:ed:55:66:42:78:a8:ee:09:ff:57:33:7e:e4]
            X: b4e2f8b9b8e4111622b2650de1acae5968c66fce005ca82a884d89c04e803d24
            Y: 4a2030c7d7614c23f72d2351d45f6fcf47b440c6e6255871206c5bd2e91c5adb
)), ref=02F5848E8EDF0468CC2BFCFD0C8F81FC10CE729D6CFA95D660FF14D72C24F648(3)), StateAndRef(state=TransactionState(data=10 TokenType(tokenIdentifier='CandyCoupon', fractionDigits=0) issued by CandyShop held by Alice, contract=com.r3.corda.lib.tokens.contracts.FungibleTokenContract, notary=O=Notary, L=London, C=GB, encumbrance=null, constraint=SignatureAttachmentConstraint(key=EC Public Key [5a:9f:70:fd:5f:d4:26:ed:55:66:42:78:a8:ee:09:ff:57:33:7e:e4]
            X: b4e2f8b9b8e4111622b2650de1acae5968c66fce005ca82a884d89c04e803d24
            Y: 4a2030c7d7614c23f72d2351d45f6fcf47b440c6e6255871206c5bd2e91c5adb
)), ref=02F5848E8EDF0468CC2BFCFD0C8F81FC10CE729D6CFA95D660FF14D72C24F648(4))]
</pre>

Now, you can give a coupon for 10 candies to Bob:
<pre>
<i>Alice's node:</i> flow start GiveCandyCoupons couponRefsStrings: [02F5848E8EDF0468CC2BFCFD0C8F81FC10CE729D6CFA95D660FF14D72C24F648(0)], newHolderParty: Bob
</pre>
The flow will return giveness transaction id: 
<pre>
Flow completed with result: 02C643FC8608FA86B107208B6DD0B06F983DBEE588BC496AA7A9D9FC5764293D
</pre>

Now, verify if the exchange transaction was added to the back-chain:
<pre>
<i>Alice's node:</i> flow start GetTransactionBackChain transactionId: 02C643FC8608FA86B107208B6DD0B06F983DBEE588BC496AA7A9D9FC5764293D
</pre>
Back-chain should contain 3 transaction ids now (issuance, exchange and giveness):
<pre>
Flow completed with result: [02C643FC8608FA86B107208B6DD0B06F983DBEE588BC496AA7A9D9FC5764293D, 02F5848E8EDF0468CC2BFCFD0C8F81FC10CE729D6CFA95D660FF14D72C24F648, 7CFE1FFB9FFFDC2C904F32A8CF7836A1388E762F755177D37729805B58EB7EAC]
</pre>

Then, transfer coupons between Alice and Bob, or exchange coupons for different quantity coupons a few times to make 
transaction back-chain longer (you will have to list coupons to get their references). Remember you can transfer many 
coupons at once.

Next, list available tokens again:
<pre>
<i>Alice's node:</i> flow start ListCandyCoupons holderParty: Alice, encumbered: null, couponRefs: null
</pre>

Here is an output you could see:
<pre>
Flow completed with result: [StateAndRef(state=TransactionState(data=10 TokenType(tokenIdentifier='CandyCoupon', fractionDigits=0) issued by CandyShop held by Alice, contract=com.r3.corda.lib.tokens.contracts.FungibleTokenContract, notary=O=Notary, L=London, C=GB, encumbrance=null, constraint=SignatureAttachmentConstraint(key=EC Public Key [5a:9f:70:fd:5f:d4:26:ed:55:66:42:78:a8:ee:09:ff:57:33:7e:e4]
            X: b4e2f8b9b8e4111622b2650de1acae5968c66fce005ca82a884d89c04e803d24
            Y: 4a2030c7d7614c23f72d2351d45f6fcf47b440c6e6255871206c5bd2e91c5adb
)), ref=02F5848E8EDF0468CC2BFCFD0C8F81FC10CE729D6CFA95D660FF14D72C24F648(3)), StateAndRef(state=TransactionState(data=10 TokenType(tokenIdentifier='CandyCoupon', fractionDigits=0) issued by CandyShop held by Alice, contract=com.r3.corda.lib.tokens.contracts.FungibleTokenContract, notary=O=Notary, L=London, C=GB, encumbrance=null, constraint=SignatureAttachmentConstraint(key=EC Public Key [5a:9f:70:fd:5f:d4:26:ed:55:66:42:78:a8:ee:09:ff:57:33:7e:e4]
            X: b4e2f8b9b8e4111622b2650de1acae5968c66fce005ca82a884d89c04e803d24
            Y: 4a2030c7d7614c23f72d2351d45f6fcf47b440c6e6255871206c5bd2e91c5adb
)), ref=E81AF7DFF5D27D106DD23958731FA255BF196F97A7054CA458F25DFB90C63A7D(0)), StateAndRef(state=TransactionState(data=10 TokenType(tokenIdentifier='CandyCoupon', fractionDigits=0) issued by CandyShop held by Alice, contract=com.r3.corda.lib.tokens.contracts.FungibleTokenContract, notary=O=Notary, L=London, C=GB, encumbrance=null, constraint=SignatureAttachmentConstraint(key=EC Public Key [5a:9f:70:fd:5f:d4:26:ed:55:66:42:78:a8:ee:09:ff:57:33:7e:e4]
            X: b4e2f8b9b8e4111622b2650de1acae5968c66fce005ca82a884d89c04e803d24
            Y: 4a2030c7d7614c23f72d2351d45f6fcf47b440c6e6255871206c5bd2e91c5adb
)), ref=E81AF7DFF5D27D106DD23958731FA255BF196F97A7054CA458F25DFB90C63A7D(1))]
</pre>

Note that secure hashes (transaction ids) in state references might be the same or might differ. They are the same 
if they were generated in the same transaction and different otherwise. Now check back-chains of available coupons:
<pre>
<i>Alice's node:</i> flow start GetTransactionBackChain transactionId: 02F5848E8EDF0468CC2BFCFD0C8F81FC10CE729D6CFA95D660FF14D72C24F648
<i>Alice's node:</i> flow start GetTransactionBackChain transactionId: E81AF7DFF5D27D106DD23958731FA255BF196F97A7054CA458F25DFB90C63A7D
</pre>
One of the back-chains should be a subset of the other one: <!-- TODO: explain -->
<pre>
Flow completed with result: [02F5848E8EDF0468CC2BFCFD0C8F81FC10CE729D6CFA95D660FF14D72C24F648, 7CFE1FFB9FFFDC2C904F32A8CF7836A1388E762F755177D37729805B58EB7EAC]
<pre>
Flow completed with result: [E81AF7DFF5D27D106DD23958731FA255BF196F97A7054CA458F25DFB90C63A7D, 566B79B8D60E1077D19C48FC5951A578BD5962B8B2F6F27A220F22A9DD5CE000, 0E0DC8E7591BB5578EC6D4DD8D5E2BF6B846821868F1D76B2034A7E4F8A704BB, 02C643FC8608FA86B107208B6DD0B06F983DBEE588BC496AA7A9D9FC5764293D, 02F5848E8EDF0468CC2BFCFD0C8F81FC10CE729D6CFA95D660FF14D72C24F648, 7CFE1FFB9FFFDC2C904F32A8CF7836A1388E762F755177D37729805B58EB7EAC]
</pre>

### Re-issuance use cases

#### Successful re-issuance

Someone might want to hide a fact that the coupon was transferred many times. To prune the back-chain, coupons
can be re-issued. Start with creating a re-issuance request:
<pre>
<i>Alice's node:</i> flow start RequestCandyCouponReIssuanceAndShareRequiredTransactions issuer: CandyShop, stateRefStringsToReIssue: [02F5848E8EDF0468CC2BFCFD0C8F81FC10CE729D6CFA95D660FF14D72C24F648(3), E81AF7DFF5D27D106DD23958731FA255BF196F97A7054CA458F25DFB90C63A7D(0), E81AF7DFF5D27D106DD23958731FA255BF196F97A7054CA458F25DFB90C63A7D(1)]
</pre>
The flow will return re-issuance request transaction id:
<pre>
Flow completed with result: C70355369E941A530089552C36D2DE07E51D4BE898788B7FCED27BC7B82B6875
</pre>

Then CandyShop has 2 options: accept the request or reject it. We will focus on acceptance in this use-case. 
Run the following command to list all re-issuance requests:
<pre>
<i>CandyShop's node:</i> run vaultQuery contractStateType: com.r3.corda.lib.reissuance.states.ReIssuanceRequest
</pre>

There should be exactly one re-issuance request, and the output of the above command should be similar to the following:
<pre>
states:
- state:
    data: !<com.r3.corda.lib.reissuance.states.ReIssuanceRequest>
      issuer: "O=CandyShop, L=London, C=GB"
      requester: "O=Alice, L=New York, C=US"
      stateRefsToReIssue:
      - txhash: "02F5848E8EDF0468CC2BFCFD0C8F81FC10CE729D6CFA95D660FF14D72C24F648"
        index: 3
      - txhash: "E81AF7DFF5D27D106DD23958731FA255BF196F97A7054CA458F25DFB90C63A7D"
        index: 0
      - txhash: "E81AF7DFF5D27D106DD23958731FA255BF196F97A7054CA458F25DFB90C63A7D"
        index: 1
      assetIssuanceCommand:
        token:
          issuer: "O=CandyShop, L=London, C=GB"
          tokenType:
            tokenIdentifier: "CandyCoupon"
            fractionDigits: 0
        outputs:
        - 0
        - 1
        - 2
      assetIssuanceSigners:
      - "O=CandyShop, L=London, C=GB"
      - "O=CandyShop, L=London, C=GB"
    contract: "com.r3.corda.lib.reissuance.contracts.ReIssuanceRequestContract"
    notary: "O=Notary, L=London, C=GB"
    encumbrance: null
    constraint: !<net.corda.core.contracts.SignatureAttachmentConstraint>
      key: "aSq9DsNNvGhYxYyqA9wd2eduEAZ5AXWgJTbTEw3G5d2maAq8vtLE4kZHgCs5jcB1N31cx1hpsLeqG2ngSysVHqcXhbNts6SkRWDaV7xNcr6MtcbufGUchxredBb6"
  ref:
    txhash: "C70355369E941A530089552C36D2DE07E51D4BE898788B7FCED27BC7B82B6875"
    index: 0
statesMetadata:
- ref:
    txhash: "C70355369E941A530089552C36D2DE07E51D4BE898788B7FCED27BC7B82B6875"
    index: 0
  contractStateClassName: "com.r3.corda.lib.reissuance.states.ReIssuanceRequest"
  recordedTime: "2020-10-14T08:21:44.152Z"
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
<i>CandyShop's node:</i> flow start ReIssueCandyCoupons reIssuanceRequestRefString: C70355369E941A530089552C36D2DE07E51D4BE898788B7FCED27BC7B82B6875(0)
</pre>
The flow will return re-issuance transaction id:
<pre>
Flow completed with result: 69DA870A2939C4D1C78C0492D721F04D5D5F7931D76ADB19BCC0B3C1382E80C6
</pre>

Now, list available tokens to make sure new tokens had been re-issued before exiting original states from the vault:
<pre>
<i>Alice's node:</i> flow start ListCandyCoupons holderParty: Alice, encumbered: null, couponRefs: null
</pre>

You should see both original states and their duplicates. Note that original states are unencumbered, and 
the duplicated states are encumbered.
<pre>
Flow completed with result: [StateAndRef(state=TransactionState(data=10 TokenType(tokenIdentifier='CandyCoupon', fractionDigits=0) issued by CandyShop held by Alice, contract=com.r3.corda.lib.tokens.contracts.FungibleTokenContract, notary=O=Notary, L=London, C=GB, encumbrance=null, constraint=SignatureAttachmentConstraint(key=EC Public Key [5a:9f:70:fd:5f:d4:26:ed:55:66:42:78:a8:ee:09:ff:57:33:7e:e4]
            X: b4e2f8b9b8e4111622b2650de1acae5968c66fce005ca82a884d89c04e803d24
            Y: 4a2030c7d7614c23f72d2351d45f6fcf47b440c6e6255871206c5bd2e91c5adb
)), ref=02F5848E8EDF0468CC2BFCFD0C8F81FC10CE729D6CFA95D660FF14D72C24F648(3)), StateAndRef(state=TransactionState(data=10 TokenType(tokenIdentifier='CandyCoupon', fractionDigits=0) issued by CandyShop held by Alice, contract=com.r3.corda.lib.tokens.contracts.FungibleTokenContract, notary=O=Notary, L=London, C=GB, encumbrance=null, constraint=SignatureAttachmentConstraint(key=EC Public Key [5a:9f:70:fd:5f:d4:26:ed:55:66:42:78:a8:ee:09:ff:57:33:7e:e4]
            X: b4e2f8b9b8e4111622b2650de1acae5968c66fce005ca82a884d89c04e803d24
            Y: 4a2030c7d7614c23f72d2351d45f6fcf47b440c6e6255871206c5bd2e91c5adb
)), ref=E81AF7DFF5D27D106DD23958731FA255BF196F97A7054CA458F25DFB90C63A7D(0)), StateAndRef(state=TransactionState(data=10 TokenType(tokenIdentifier='CandyCoupon', fractionDigits=0) issued by CandyShop held by Alice, contract=com.r3.corda.lib.tokens.contracts.FungibleTokenContract, notary=O=Notary, L=London, C=GB, encumbrance=null, constraint=SignatureAttachmentConstraint(key=EC Public Key [5a:9f:70:fd:5f:d4:26:ed:55:66:42:78:a8:ee:09:ff:57:33:7e:e4]
            X: b4e2f8b9b8e4111622b2650de1acae5968c66fce005ca82a884d89c04e803d24
            Y: 4a2030c7d7614c23f72d2351d45f6fcf47b440c6e6255871206c5bd2e91c5adb
)), ref=E81AF7DFF5D27D106DD23958731FA255BF196F97A7054CA458F25DFB90C63A7D(1)), StateAndRef(state=TransactionState(data=10 TokenType(tokenIdentifier='CandyCoupon', fractionDigits=0) issued by CandyShop held by Alice, contract=com.r3.corda.lib.tokens.contracts.FungibleTokenContract, notary=O=Notary, L=London, C=GB, encumbrance=1, constraint=SignatureAttachmentConstraint(key=EC Public Key [5a:9f:70:fd:5f:d4:26:ed:55:66:42:78:a8:ee:09:ff:57:33:7e:e4]
            X: b4e2f8b9b8e4111622b2650de1acae5968c66fce005ca82a884d89c04e803d24
            Y: 4a2030c7d7614c23f72d2351d45f6fcf47b440c6e6255871206c5bd2e91c5adb
)), ref=69DA870A2939C4D1C78C0492D721F04D5D5F7931D76ADB19BCC0B3C1382E80C6(0)), StateAndRef(state=TransactionState(data=10 TokenType(tokenIdentifier='CandyCoupon', fractionDigits=0) issued by CandyShop held by Alice, contract=com.r3.corda.lib.tokens.contracts.FungibleTokenContract, notary=O=Notary, L=London, C=GB, encumbrance=2, constraint=SignatureAttachmentConstraint(key=EC Public Key [5a:9f:70:fd:5f:d4:26:ed:55:66:42:78:a8:ee:09:ff:57:33:7e:e4]
            X: b4e2f8b9b8e4111622b2650de1acae5968c66fce005ca82a884d89c04e803d24
            Y: 4a2030c7d7614c23f72d2351d45f6fcf47b440c6e6255871206c5bd2e91c5adb
)), ref=69DA870A2939C4D1C78C0492D721F04D5D5F7931D76ADB19BCC0B3C1382E80C6(1)), StateAndRef(state=TransactionState(data=10 TokenType(tokenIdentifier='CandyCoupon', fractionDigits=0) issued by CandyShop held by Alice, contract=com.r3.corda.lib.tokens.contracts.FungibleTokenContract, notary=O=Notary, L=London, C=GB, encumbrance=3, constraint=SignatureAttachmentConstraint(key=EC Public Key [5a:9f:70:fd:5f:d4:26:ed:55:66:42:78:a8:ee:09:ff:57:33:7e:e4]
            X: b4e2f8b9b8e4111622b2650de1acae5968c66fce005ca82a884d89c04e803d24
            Y: 4a2030c7d7614c23f72d2351d45f6fcf47b440c6e6255871206c5bd2e91c5adb
)), ref=69DA870A2939C4D1C78C0492D721F04D5D5F7931D76ADB19BCC0B3C1382E80C6(2))]
</pre>

Newly generated states, even though they are available, can't be spent without re-issuance lock:
<pre>
<i>Alice's node:</i> flow start GiveCandyCoupons newHolderParty: Bob, couponRefsStrings: [69DA870A2939C4D1C78C0492D721F04D5D5F7931D76ADB19BCC0B3C1382E80C6(0)] 
</pre>
You should see an error message:
<pre>
☠   java.lang.IndexOutOfBoundsException: Index: 0, Size: 0
</pre>

Run the following command to list re-issuance locks:
<pre>
<i>Alice's node:</i> run vaultQuery contractStateType: com.r3.corda.lib.reissuance.states.ReIssuanceLock
</pre>
There should be exactly one re-issuance lock available: 
<pre>
states:
- state:
    data: !<com.r3.corda.lib.reissuance.states.ReIssuanceLock>
      issuer: "O=CandyShop, L=London, C=GB"
      requester: "O=Alice, L=New York, C=US"
      originalStates:
      - state:
          data: !<com.r3.corda.lib.tokens.contracts.states.FungibleToken>
            amount: "10 TokenType(tokenIdentifier='CandyCoupon', fractionDigits=0)\
              \ issued by CandyShop"
            holder: "O=Alice, L=New York, C=US"
            tokenTypeJarHash: null
          contract: "com.r3.corda.lib.tokens.contracts.FungibleTokenContract"
          notary: "O=Notary, L=London, C=GB"
          encumbrance: null
          constraint: !<net.corda.core.contracts.SignatureAttachmentConstraint>
            key: "aSq9DsNNvGhYxYyqA9wd2eduEAZ5AXWgJTbTJUgKok6g1qtLzJhoTKPioLoBfSTZU2Eg4sCfe9nn1urJJdE2r1pGVVVTDWJqs3EEb4n9tsXDwyYUYHMPT4XqgkKc"
        ref:
          txhash: "02F5848E8EDF0468CC2BFCFD0C8F81FC10CE729D6CFA95D660FF14D72C24F648"
          index: 3
      - state:
          data: !<com.r3.corda.lib.tokens.contracts.states.FungibleToken>
            amount: "10 TokenType(tokenIdentifier='CandyCoupon', fractionDigits=0)\
              \ issued by CandyShop"
            holder: "O=Alice, L=New York, C=US"
            tokenTypeJarHash: null
          contract: "com.r3.corda.lib.tokens.contracts.FungibleTokenContract"
          notary: "O=Notary, L=London, C=GB"
          encumbrance: null
          constraint: !<net.corda.core.contracts.SignatureAttachmentConstraint>
            key: "aSq9DsNNvGhYxYyqA9wd2eduEAZ5AXWgJTbTJUgKok6g1qtLzJhoTKPioLoBfSTZU2Eg4sCfe9nn1urJJdE2r1pGVVVTDWJqs3EEb4n9tsXDwyYUYHMPT4XqgkKc"
        ref:
          txhash: "E81AF7DFF5D27D106DD23958731FA255BF196F97A7054CA458F25DFB90C63A7D"
          index: 0
      - state:
          data: !<com.r3.corda.lib.tokens.contracts.states.FungibleToken>
            amount: "10 TokenType(tokenIdentifier='CandyCoupon', fractionDigits=0)\
              \ issued by CandyShop"
            holder: "O=Alice, L=New York, C=US"
            tokenTypeJarHash: null
          contract: "com.r3.corda.lib.tokens.contracts.FungibleTokenContract"
          notary: "O=Notary, L=London, C=GB"
          encumbrance: null
          constraint: !<net.corda.core.contracts.SignatureAttachmentConstraint>
            key: "aSq9DsNNvGhYxYyqA9wd2eduEAZ5AXWgJTbTJUgKok6g1qtLzJhoTKPioLoBfSTZU2Eg4sCfe9nn1urJJdE2r1pGVVVTDWJqs3EEb4n9tsXDwyYUYHMPT4XqgkKc"
        ref:
          txhash: "E81AF7DFF5D27D106DD23958731FA255BF196F97A7054CA458F25DFB90C63A7D"
          index: 1
      status: "ACTIVE"
      issuerIsRequiredExitTransactionSigner: true
    contract: "com.r3.corda.lib.reissuance.contracts.ReIssuanceLockContract"
    notary: "O=Notary, L=London, C=GB"
    encumbrance: 0
    constraint: !<net.corda.core.contracts.SignatureAttachmentConstraint>
      key: "aSq9DsNNvGhYxYyqA9wd2eduEAZ5AXWgJTbTEw3G5d2maAq8vtLE4kZHgCs5jcB1N31cx1hpsLeqG2ngSysVHqcXhbNts6SkRWDaV7xNcr6MtcbufGUchxredBb6"
  ref:
    txhash: "69DA870A2939C4D1C78C0492D721F04D5D5F7931D76ADB19BCC0B3C1382E80C6"
    index: 3
statesMetadata:
- ref:
    txhash: "69DA870A2939C4D1C78C0492D721F04D5D5F7931D76ADB19BCC0B3C1382E80C6"
    index: 3
  contractStateClassName: "com.r3.corda.lib.reissuance.states.ReIssuanceLock"
  recordedTime: "2020-10-14T08:26:28.939Z"
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
<i>Alice's node:</i> flow start ThrowAwayCandyCoupons couponRefsStrings: [02F5848E8EDF0468CC2BFCFD0C8F81FC10CE729D6CFA95D660FF14D72C24F648(3), E81AF7DFF5D27D106DD23958731FA255BF196F97A7054CA458F25DFB90C63A7D(0), E81AF7DFF5D27D106DD23958731FA255BF196F97A7054CA458F25DFB90C63A7D(1)]
</pre>
The flow will return id of the exit transaction:
<pre>
Flow completed with result: FFC2881F870778D66071FCF72A5CBE2ECED5B7562CD7D02C8E08EEAA7F5DA0E8
</pre>

Next, upload exit transaction as an attachment providing its id:
<pre>
<i>Alice's node:</i> flow start UploadTransactionAsAttachment transactionId: FFC2881F870778D66071FCF72A5CBE2ECED5B7562CD7D02C8E08EEAA7F5DA0E8
</pre>
The flow will return newly created attachment id:
<pre>
Flow completed with result: 4C72E5739E3BEB88D59699029A2B2BF6A328737597EF26D9809CAC1CC43EF7C7
</pre>

Then you can unlock re-issued states, providing their references, re-issuance lock reference, and attachment id of 
token exit transaction:
<pre>
<i>Alice's node:</i> 
flow start UnlockReIssuedCandyCoupons reIssuedStatesRefStrings: [69DA870A2939C4D1C78C0492D721F04D5D5F7931D76ADB19BCC0B3C1382E80C6(0), 69DA870A2939C4D1C78C0492D721F04D5D5F7931D76ADB19BCC0B3C1382E80C6(1), 69DA870A2939C4D1C78C0492D721F04D5D5F7931D76ADB19BCC0B3C1382E80C6(2)], reIssuanceLockRefString: 69DA870A2939C4D1C78C0492D721F04D5D5F7931D76ADB19BCC0B3C1382E80C6(3), deletedStateTransactionHashes: [4C72E5739E3BEB88D59699029A2B2BF6A328737597EF26D9809CAC1CC43EF7C7]
</pre>
The flows will return the id of unlock re-issued states transaction:
<pre>
Flow completed with result: 313A033B99D3A8B3E27B49C8886CD104C818CFF2AB02BC8B11B312BEE3D03EA6
</pre>

Now list tokens one more time:
<pre>
<i>Alice's node:</i> flow start ListCandyCoupons holderParty: Alice, encumbered: null, couponRefs: null
</pre>
Note that the re-issued states are now unencumbered:
<pre>
Flow completed with result: [StateAndRef(state=TransactionState(data=10 TokenType(tokenIdentifier='CandyCoupon', fractionDigits=0) issued by CandyShop held by Alice, contract=com.r3.corda.lib.tokens.contracts.FungibleTokenContract, notary=O=Notary, L=London, C=GB, encumbrance=null, constraint=SignatureAttachmentConstraint(key=EC Public Key [5a:9f:70:fd:5f:d4:26:ed:55:66:42:78:a8:ee:09:ff:57:33:7e:e4]
            X: b4e2f8b9b8e4111622b2650de1acae5968c66fce005ca82a884d89c04e803d24
            Y: 4a2030c7d7614c23f72d2351d45f6fcf47b440c6e6255871206c5bd2e91c5adb
)), ref=313A033B99D3A8B3E27B49C8886CD104C818CFF2AB02BC8B11B312BEE3D03EA6(0)), StateAndRef(state=TransactionState(data=10 TokenType(tokenIdentifier='CandyCoupon', fractionDigits=0) issued by CandyShop held by Alice, contract=com.r3.corda.lib.tokens.contracts.FungibleTokenContract, notary=O=Notary, L=London, C=GB, encumbrance=null, constraint=SignatureAttachmentConstraint(key=EC Public Key [5a:9f:70:fd:5f:d4:26:ed:55:66:42:78:a8:ee:09:ff:57:33:7e:e4]
            X: b4e2f8b9b8e4111622b2650de1acae5968c66fce005ca82a884d89c04e803d24
            Y: 4a2030c7d7614c23f72d2351d45f6fcf47b440c6e6255871206c5bd2e91c5adb
)), ref=313A033B99D3A8B3E27B49C8886CD104C818CFF2AB02BC8B11B312BEE3D03EA6(1)), StateAndRef(state=TransactionState(data=10 TokenType(tokenIdentifier='CandyCoupon', fractionDigits=0) issued by CandyShop held by Alice, contract=com.r3.corda.lib.tokens.contracts.FungibleTokenContract, notary=O=Notary, L=London, C=GB, encumbrance=null, constraint=SignatureAttachmentConstraint(key=EC Public Key [5a:9f:70:fd:5f:d4:26:ed:55:66:42:78:a8:ee:09:ff:57:33:7e:e4]
            X: b4e2f8b9b8e4111622b2650de1acae5968c66fce005ca82a884d89c04e803d24
            Y: 4a2030c7d7614c23f72d2351d45f6fcf47b440c6e6255871206c5bd2e91c5adb
)), ref=313A033B99D3A8B3E27B49C8886CD104C818CFF2AB02BC8B11B312BEE3D03EA6(2))]
</pre>

Then, list back-chain of the re-issued states (all secure hashes in references are the same as they were generated by 
the same transaction):
<pre>
<i>Alice's node:</i> flow start GetTransactionBackChain transactionId: 313A033B99D3A8B3E27B49C8886CD104C818CFF2AB02BC8B11B312BEE3D03EA6
</pre>
The result will contain identifiers of 3 transactions:
- requesting re-issuance
- re-issuance
- unlocking re-issued tokens
<pre>
Flow completed with result: [313A033B99D3A8B3E27B49C8886CD104C818CFF2AB02BC8B11B312BEE3D03EA6, 69DA870A2939C4D1C78C0492D721F04D5D5F7931D76ADB19BCC0B3C1382E80C6, C70355369E941A530089552C36D2DE07E51D4BE898788B7FCED27BC7B82B6875]
</pre>

Now try to spend re-issued tokens again:
<pre>
flow start GiveCandyCoupons newHolderParty: Bob, couponRefsStrings: [313A033B99D3A8B3E27B49C8886CD104C818CFF2AB02BC8B11B312BEE3D03EA6(2)]
</pre>
As the coupons aren't encumbered anymore, the transaction should be successful:
<pre>
Flow completed with result: 0488618A3D36B2391F372C8EB38215FF5FDAD02FB0D5FCDF3AF867F6270CE65B
</pre>

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
