
@Component
public class ImageDownloadFromCompletionsExample {
    @Autowired
    private Request request;

    public Photo getPhoto(String[] args) {
        GigaChatClient client = GigaChatClient.builder()
                .authClient(AuthClient.builder()
                        .withOAuth(OAuthBuilder.builder()
                                .scope(Scope.GIGACHAT_API_CORP)
                                .authKey("MTRmYmM4YmItZDllNy00OGU3LThmNzMtOTAyMTA1YmNiMDUzOjMzZjM1ZGI4LTAzMzQtNDUxYi1iOGMzLWE1ZDllNjBmZmJhZQ==")
                                .build())
                        .build())
                .build();
        try {
            // Получаем список моделей
            var modelResponse = client.models();
            if (modelResponse != null) {
                var completionsResponse = client.completions(CompletionRequest.builder()
                        .model(modelResponse.data().get(0).id())
                        .messages(List.of(
                                ChatMessage.builder()
                                        .role(Role.SYSTEM)
                                        .content("Ты — флорист цветочного магазина")
                                        .build(),
                                ChatMessage.builder()
                                        .role(Role.USER)
                                        .content("Нарисуй букет, состав которого следующий: ")
                                        .build()))
                        .build());
                String content = completionsResponse.choices().get(0).message().content();
                return content;
            }
        } catch (HttpClientException ex) {
            System.out.println(ex.statusCode() + " " + ex.bodyAsString());
        }
    }

    private static 
}