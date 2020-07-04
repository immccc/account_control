# Account control
## Code challenge for Technest

### What has been delivered
- REST controllers based on Webflux, modelling code in a reactive way.
- Methods for
    - Find an account based by name: _GET accounts/[ACCOUNT_NAME]_
    - Store an account (it should not exist previously) : _POST accounts_ with payload
    ~~~
    {
       "name": "account_name",
       "balance": 0.00,
       "currency": "CUR",
       "treasury": false
    }
    ~~~
    
    - Modify an existing account (treasury property can´t be modified): _PUT accounts_ with payload
        ~~~
        {
           "name": "account_name",
           "balance": 0.00,
           "currency": "CUR"
        }
        ~~~
    
    - Perform a transfer between from one account to another: _POST transfers_
        ~~~
        {
           "amount": 0.00,
           "from": "account_name_from",
           "to": "account_name_to"
        }
        ~~~    
- Custom error codes per exception.    
- Embedded Redis for storage of accounts


### What I tried to achieve
- API based on Webflux and Reactor for ensuring fast response time to requester (logic is quite straightforward though)
- Immutability on entities
- Domain Driven Design approach
- TDD
- Given than multiple transfer requests on the same accounts and time could bring race conditions, concurrency has been controlled by using Reactor backpressure feature, forcing the reactive pipelines to go through a single threaded executor.

### Improvements that could have been applied, but time ran out
- To store in DB transfers and add an API for querying which transfers have been performed and when. However this has not been asked in the test, so only modifications on affected accounts are done. 
- Cleanup of a warning on startup regarding multiple SLF4J bindings. I suspect this is coming from embedded Redis dependency and solution might be to exclude a particular library on the dependency in pom. 
- Improve error on HTTP request responses, so that not only HTTP return status is shown, but a message. This could have been done by overriding DefaultErrorAttributes.
- Logging to track executions. 
