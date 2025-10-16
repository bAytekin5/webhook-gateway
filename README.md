# Webhook Gateway

Spring Boot tabanlı bu proje, gelen ödeme webhook’larını güvenli ve dayanıklı bir şekilde kuyruğa alıp arka planda işler.

## Bileşenler
- Spring Boot REST API (`/api/webhooks/payment`)
- RabbitMQ ana kuyruk ve Dead-Letter Queue
- Redis tabanlı idempotency kontrolü
- Docker Compose ile kolay kurulum

## Hızlı Başlangıç
```bash
mvn clean package
docker-compose up -d --build
```
- API: `http://localhost:8080`
- RabbitMQ UI: `http://localhost:15672` (guest / guest)
- Health: `http://localhost:8080/actuator/health`
- Info: `http://localhost:8080/actuator/info`
- DLQ Retry API: `POST http://localhost:8080/api/admin/dlq/retry` (örnek istek: `{ "messageCount": 5, "targetQueue": "webhook-queue" }`)
- Postman koleksiyonu: `postman/WebhookGateway.postman_collection.json`

## Örnek İstekler
- Başarılı işlem (202): `transactionId` benzersiz, `paymentStatus=SUCCESS`
- Tekrar gönderim (200): Aynı `transactionId` ikinci kez gönderildiğinde Redis idempotency devreye girer
- Hatalı işlem (DLQ): `paymentStatus=FAIL` -> mesaj `webhook-dlq-queue` içine düşer

## Postman Senaryoları
- `Webhook Payment Success` ve `Webhook Payment Failure (DLQ)`
- `Webhook Idempotent Repeat` duplikasyon kontrolü
- `Webhook Validation Error` doğrulama hatası örneği
- `DLQ Retry`, `DLQ Retry Invalid Count`, `DLQ Retry Empty Queue`
- `Health Endpoint`, `Info Endpoint`

## İzleme ve Log
```bash
docker logs -f psp-app
docker exec rabbitmq rabbitmqadmin get queue=webhook-dlq-queue
curl http://localhost:8080/actuator/health
make dlq-get      # DLQ mesajlarını görüntüler
make dlq-purge    # DLQ'yi boşaltır
```

