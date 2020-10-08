<p align="center">	
  <img src="https://www.corda.net/wp-content/uploads/2016/11/fg005_corda_b.png" alt="Corda" width="500">	
</p>

# Re-issuance Sample CorDapp 
This CorDapp is re-issuance demo and demonstrates how to use re-issuance flows.

Re-issuance CorDapp: https://github.com/corda/reissue-cordapp

## Running the sample

### Deploy and run the nodes
Run the following commands:
```
./gradlew deployNodes
./build/nodes/runnodes
```
If you have any questions during setup, please go to https://docs.corda.net/getting-set-up.html for detailed setup instructions.

### Generate transaction back-chain
Once all nodes are started up (Notary, Issuer, Alice & Bob), create a token asset:
<pre>
<i>Issuer's node:</i> flow start IssueDemoAppTokens tokenHolderParty: Alice, tokenAmount: 50
</pre>

To list the issued tokens, run the following:
<pre>
<i>Alice's node:</i> flow start ListAvailableDemoAppTokens holderParty: Alice, encumbered: null
</pre>

You should see output similar to the following one:
<pre>
Flow completed with result: [StateAndRef(state=TransactionState(data=<b>50 TokenType(tokenIdentifier='DemoAppToken', fractionDigits=0) issued by Issuer held by Alice</b>, contract=com.r3.corda.lib.tokens.contracts.FungibleTokenContract, notary=O=Notary, L=London, C=GB, encumbrance=null, constraint=SignatureAttachmentConstraint(key=EC Public Key [5a:9f:70:fd:5f:d4:26:ed:55:66:42:78:a8:ee:09:ff:57:33:7e:e4]
            X: b4e2f8b9b8e4111622b2650de1acae5968c66fce005ca82a884d89c04e803d24
            Y: 4a2030c7d7614c23f72d2351d45f6fcf47b440c6e6255871206c5bd2e91c5adb
)), ref=84A3BD0200731762EFEF46F1013DFB47B1067A5C112A3A852CE6E7FF5E51302C(0))]
</pre>

Now you can check issued token back-chain:
<pre>
<i>Alice's node:</i> flow start GetTransactionBackChain transactionId: DD5FFC0D2C255821DB4BF9FC474A878E760C4D7A4C810CD5E0D12E550D8467C8
</pre>
It should contain only issuance transaction id:
<pre>
Flow completed with result: [DD5FFC0D2C255821DB4BF9FC474A878E760C4D7A4C810CD5E0D12E550D8467C8]
</pre>

Then, transfer 30 tokens to Bob:
<pre>
<i>Alice's node:</i> flow start MoveDemoAppTokens issuer: Issuer, newTokenHolderParty: Bob, tokenAmount: 30 
</pre>

Now verify if the transaction was added to the back-chain:
<pre>
<i>Bob's node:</i> flow start GetTransactionBackChain transactionId: DD5FFC0D2C255821DB4BF9FC474A878E760C4D7A4C810CD5E0D12E550D8467C8
</pre>
Back-chain should contain 2 transaction ids now:
<pre>
Flow completed with result: [DD5FFC0D2C255821DB4BF9FC474A878E760C4D7A4C810CD5E0D12E550D8467C8, DD5FFC0D2C255821DB4BF9FC474A878E760C4D7A4C810CD5E0D12E550D8467C8]
</pre>

Then, transfer the tokens between Alice and Bob many times make transaction back-chain longer:
<pre>
<i>Bob's node:</i> flow start MoveDemoAppTokens issuer: Issuer, newTokenHolderParty: Alice, tokenAmount: 20 
<i>Alice's node:</i> flow start MoveDemoAppTokens issuer: Issuer, newTokenHolderParty: Bob, tokenAmount: 15
<i>Bob's node:</i> flow start MoveDemoAppTokens issuer: Issuer, newTokenHolderParty: Alice, tokenAmount: 25
<i>Alice's node:</i> flow start MoveDemoAppTokens issuer: Issuer, newTokenHolderParty: Bob, tokenAmount: 35
<i>Bob's node:</i> flow start MoveDemoAppTokens issuer: Issuer, newTokenHolderParty: Alice, tokenAmount: 10
</pre>

Next, list available tokens again:
```
flow start ListAvailableDemoAppTokens holderParty: Alice, encumbered: null
```
This time, you should see more 2 tokens:
<pre>
Flow completed with result: [StateAndRef(state=TransactionState(<b>data=15 TokenType(tokenIdentifier='DemoAppToken', fractionDigits=0) issued by Issuer held by Alice</b>, contract=com.r3.corda.lib.tokens.contracts.FungibleTokenContract, notary=O=Notary, L=London, C=GB, encumbrance=null, constraint=SignatureAttachmentConstraint(key=EC Public Key [5a:9f:70:fd:5f:d4:26:ed:55:66:42:78:a8:ee:09:ff:57:33:7e:e4]
            X: b4e2f8b9b8e4111622b2650de1acae5968c66fce005ca82a884d89c04e803d24
            Y: 4a2030c7d7614c23f72d2351d45f6fcf47b440c6e6255871206c5bd2e91c5adb
)), ref=798D77C4019B3E14FD3F0E47D2BF5EC8DEA107D9B72F4C63C0EA0B854BEB3E16(1)), StateAndRef(state=TransactionState(data=<b>10 TokenType(tokenIdentifier='DemoAppToken', fractionDigits=0) issued by Issuer held by Alice</b>, contract=com.r3.corda.lib.tokens.contracts.FungibleTokenContract, notary=O=Notary, L=London, C=GB, encumbrance=null, constraint=SignatureAttachmentConstraint(key=EC Public Key [5a:9f:70:fd:5f:d4:26:ed:55:66:42:78:a8:ee:09:ff:57:33:7e:e4]
            X: b4e2f8b9b8e4111622b2650de1acae5968c66fce005ca82a884d89c04e803d24
            Y: 4a2030c7d7614c23f72d2351d45f6fcf47b440c6e6255871206c5bd2e91c5adb
)), ref=2A386D8B9E91CB50EFE5A2B8A29EEEAA4632B3CBC147E979B9483FC32DAAB370(0))]
</pre>

Now check back-chains of both of them:
<pre>
<i>Alice's node:</i> flow start GetTransactionBackChain transactionId: DD5FFC0D2C255821DB4BF9FC474A878E760C4D7A4C810CD5E0D12E550D8467C8
</pre>

You should see a list of 7 transaction ids:
<pre>
Flow completed with result: [D6DBD174C7C50DDC9F2D8E07F0D23AE9D09DFAC86B5DA1FA6A109B6DAF153192, F7292CDE87A677D7E15248354060A67EB9B4A57B653BBADFCC0C903B098AEA25, DD5FFC0D2C255821DB4BF9FC474A878E760C4D7A4C810CD5E0D12E550D8467C8]
</pre>

<pre>
<i>Alice's node:</i> flow start GetTransactionBackChain transactionId: DD5FFC0D2C255821DB4BF9FC474A878E760C4D7A4C810CD5E0D12E550D8467C8
</pre>

You should see a list of 5 transaction ids which is a subset of the first transaction back-chain:
<pre>
Flow completed with result: [D6DBD174C7C50DDC9F2D8E07F0D23AE9D09DFAC86B5DA1FA6A109B6DAF153192, F7292CDE87A677D7E15248354060A67EB9B4A57B653BBADFCC0C903B098AEA25, DD5FFC0D2C255821DB4BF9FC474A878E760C4D7A4C810CD5E0D12E550D8467C8]
</pre>

### Re-issue tokens

To prune the back-chain, tokens can be re-issued. Start with creating a re-issuance request:
<pre>
<i>Alice's node:</i> 
</pre>

