# Spring Boot ile GraphQL API

[bilisim.io](https://bilisim.io/2020/02/17/spring-boot-ile-graphql-api-1/) üzerindeki "Spring Boot ile GraphQL API" yazı dizisinin örnek projesi. Blog yazarları (`Blogger`), bloglar (`Blog`) ve kategoriler (`Category`) üzerinde sorgu, mutation ve subscription işlemleri sunan bir GraphQL API'sidir.

## Teknolojiler

| Bileşen | Sürüm |
|---|---|
| Java | 21 (LTS) |
| Spring Boot | 4.1.2 |
| Spring for GraphQL | 2.0.x (graphql-java 25) |
| Gradle | 9.8.2 (wrapper) |
| RabbitMQ (opsiyonel) | Spring AMQP 4.1 |

Veriler bellekte tutulur; uygulama her açılışta örnek verilerle (8 blogger, 5 blog, 3 kategori) başlar.

## Çalıştırma

```bash
./gradlew bootRun
```

| Adres | Açıklama |
|---|---|
| http://localhost:8080/graphiql | Tarayıcıda GraphiQL arayüzü |
| http://localhost:8080/graphql | GraphQL HTTP uç noktası (POST) |
| ws://localhost:8080/graphql-ws | Subscription'lar için WebSocket (graphql-ws protokolü) |
| http://localhost:8080/graphql/schema | Şemanın tamamı (SDL) |
| http://localhost:8080/actuator/health | Sağlık kontrolü |

### Docker

Uygulamayı RabbitMQ ile birlikte ayağa kaldırmak için:

```bash
docker compose up --build
```

RabbitMQ yönetim paneli: http://localhost:15672 (guest / guest).

## Testler

```bash
./gradlew test
```

Entegrasyon testleri `GraphQlTester` ile tüm sorgu, mutation, hata ve subscription senaryolarını uçtan uca çalıştırır.

## Şema

Şema dosyaları `src/main/resources/graphql/` altındadır.

### Sorgular

| Sorgu | Açıklama |
|---|---|
| `bloggers` | Tüm blogger'lar |
| `blogger(id)` | Tek blogger; yoksa `null` |
| `blog(id)` | Tek blog; yoksa `null`. Bulunursa `VIEWED` olayı yayınlanır |
| `blogs(page, size)` | Sayfalı blog listesi (`page` 0'dan başlar, `size` 1–100) |
| `searchBlogs(keyword)` | Başlık ve içerikte büyük/küçük harf ve Türkçe I/ı/İ/i duyarsız arama |
| `blogsByCategory(categoryId)` | Kategoriye göre bloglar |
| `categories`, `category(id)` | Kategoriler |

### Mutation'lar

| Mutation | Açıklama |
|---|---|
| `createBlogger(input)` | Yeni blogger. E-posta biçimi, benzersizliği ve parola uzunluğu (≥ 6) doğrulanır |
| `updateBlogger(id, email, name, surname, gender, address)` | Kısmi güncelleme: yalnızca gönderilen alanlar değişir |
| `deleteBlogger(id)` | Blogger'ı siler |
| `createBlog(input)` | Yeni blog. Yazar ve kategori id'leri doğrulanır; `blogPublished` subscription'ını tetikler |
| `updateBlog(id, input)` | Kısmi güncelleme |
| `deleteBlog(id)` | Blogu siler |
| `createCategory(name)` | Yeni kategori (aynı ad tekrar eklenemez) |

### Subscription'lar

| Subscription | Açıklama |
|---|---|
| `blogPublished` | Yeni bir blog oluşturulduğunda yayınlanır |

### Kullanımdan kaldırılanlar

Eski istemciler bozulmasın diye ilk sürümdeki işlemler hâlâ çalışır, ancak şemada `@deprecated` olarak işaretlidir:

| Eski | Yeni |
|---|---|
| `getAllBloggers` | `bloggers` |
| `getBlogger(id)` | `blogger(id)` |
| `getBlogById(id)` | `blog(id)` |
| `getAllBlogs` | `blogs(page, size)` |
| `saveBlogger(...)` | `createBlogger(input)` |

## Örnekler

Yazar, blogları ve kategorileri tek sorguda:

```graphql
{
  blogger(id: 8) {
    name
    surname
    blogList {
      title
      publishedAt
      categoryList { name }
    }
  }
}
```

Sayfalama:

```graphql
{
  blogs(page: 0, size: 2) {
    content { id title author { name } }
    totalElements
    totalPages
    hasNext
  }
}
```

Blogger oluşturma:

```graphql
mutation {
  createBlogger(input: {
    name: "Ada"
    surname: "Lovelace"
    email: "ada@bilisim.io"
    password: "secret123"
    gender: FEMALE
    address: { addressType: HOME, addressField: "Londra" }
  }) {
    id
    name
  }
}
```

Blog oluşturma:

```graphql
mutation {
  createBlog(input: {
    title: "GraphQL Subscriptions"
    content: "WebSocket ile gerçek zamanlı veri"
    authorId: 8
    categoryIds: [1, 3]
  }) {
    id
    author { name }
    categoryList { name }
  }
}
```

Yeni blogları dinleme (GraphiQL'de ayrı bir sekmede çalıştırın, ardından `createBlog` çağırın):

```graphql
subscription {
  blogPublished { id title author { name } }
}
```

`curl` ile:

```bash
curl -s http://localhost:8080/graphql -H 'Content-Type: application/json' -d '{"query":"{ bloggers { id name } }"}'
```

## Hata yönetimi

Hatalar `errors` dizisinde, `extensions.classification` alanıyla döner:

| Sınıf | Ne zaman |
|---|---|
| `NOT_FOUND` | Var olmayan bir kaydı güncelleme/silme, olmayan yazara blog ekleme |
| `BAD_REQUEST` | Doğrulama hataları (geçersiz e-posta, kısa parola, sayfa boyutu vb.), tekrar eden e-posta veya kategori, olmayan kategori id'si |

```json
{
  "errors": [{
    "message": "Blogger bulunamadı: id=999",
    "path": ["deleteBlogger"],
    "extensions": { "classification": "NOT_FOUND" }
  }]
}
```

## RabbitMQ entegrasyonu

Blog oluşturma, güncelleme, silme ve görüntüleme işlemleri bir `BlogEvent` (`CREATED`, `UPDATED`, `DELETED`, `VIEWED`) olarak yayınlanır.

- Varsayılan olarak **kapalıdır**; olaylar yalnızca loglanır ve uygulama RabbitMQ olmadan çalışır.
- Açmak için `APP_MESSAGING_RABBITMQ_ENABLED=true` verin. Olaylar JSON olarak `bilisimio.exchange` exchange'ine `bilisimio.routingkey` anahtarıyla gönderilir ve `bilisimio.queue` kuyruğuna düşer.
- RabbitMQ'ya ulaşılamazsa hata loglanır, GraphQL isteği başarısız olmaz.

| Ortam değişkeni | Varsayılan |
|---|---|
| `APP_MESSAGING_RABBITMQ_ENABLED` | `false` |
| `RABBITMQ_HOST` | `localhost` |
| `RABBITMQ_PORT` | `5672` |
| `RABBITMQ_USERNAME` | `guest` |
| `RABBITMQ_PASSWORD` | `guest` |

## Proje yapısı

```
src/main/java/com/bilisimio
├── config/        RabbitMQ yapılandırması ve ayarları
├── controller/    GraphQL controller'ları (@QueryMapping, @MutationMapping, @BatchMapping, ...)
├── dto/           Input tipleri ve sayfa modeli
├── entity/        Alan modelleri
├── enums/         AddressType, GenderType
├── exception/     İstisnalar ve GraphQL hata dönüşümü
├── messaging/     Blog olayları ve yayıncılar (RabbitMQ / log)
└── service/       İş kuralları ve bellek içi veri deposu
```

`Blog.author`, `Blog.categoryList` ve `Blogger.blogList` alanları `@BatchMapping` ile DataLoader üzerinden toplu yüklenir; böylece bir listedeki her öğe için ayrı sorgu atılmaz (N+1 problemi).
