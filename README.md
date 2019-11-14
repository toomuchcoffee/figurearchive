### Setup for local development

#### Start environment with DB
```
docker-compose up -d
```

#### Connect to DB
```
psql -h localhost -p 5432 -U figures -d figurearchive 
```
enter password `s3cr3t`

#### Stop environment with DB
```
docker-compose down
```
   
### Testing
Docker needs to run for the test database    
  
