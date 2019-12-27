# Spring Session Example

### Header Session ID resolver

### Cookie Session ID resolver

### Composite Session ID resolver

### CORS 

```
curl -v -X POST -H "Origin: http://foo.com" -H "Access-Control-Request-Method: POST" -H "Access-Control-Request-Headers: X-Session-Id" -F "username=admin" -F "password=admin" -X OPTIONS http://localhost:8080/login
```

```
curl -v -X POST -H "Origin: http://foo.com" -H "Access-Control-Request-Method: POST" -H "Access-Control-Request-Headers: X-Session-Id" -F "username=admin" -F "password=admin" http://localhost:8080/login
```