# java-interview-assignment
Java Backend Developer Interview

#### API Link
The API link where these functionalities are as follows

The app can be cloned ran locally and tested with postman too.

Example request to register a new card and get the details.
- https://card-verification-test.herokuapp.com/api/v1/card-scheme/verify/45717360

response: 
```json
{
    "success": true,
    "payload": {
        "scheme": "visa",
        "type": "debit",
        "bank": "Access"
    }
}
```
to get statistics of all saved cards
- https://card-verification-test.herokuapp.com/api/v1/card-scheme/stats?start=1&limit=3
default param = start=0, limit=25
```json
{
    "success": true,
    "start": Number,
    "limit": Number,
    "size": Number,
    "payload": {
      "string" : Integer (or null)
      }
}
```

Thank you for reviewing my application.

###### Frank Okafor
