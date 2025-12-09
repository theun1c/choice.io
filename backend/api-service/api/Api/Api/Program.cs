using Api.Services;

var builder = WebApplication.CreateBuilder(args);

// Конфигурация
builder.Configuration.AddJsonFile("appsettings.json", optional: false);

// Сервисы
builder.Services.AddSingleton<ISupabaseService, SupabaseService>();
builder.Services.AddScoped<IFeedService, FeedService>();

// Контроллеры
builder.Services.AddControllers();

// Swagger
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();

// CORS
builder.Services.AddCors(options =>
{
    options.AddPolicy("AllowAll", policy =>
    {
        policy.AllowAnyOrigin()
            .AllowAnyMethod()
            .AllowAnyHeader();
    });
});

var app = builder.Build();

// Swagger всегда включен (удобно для тестирования)
app.UseSwagger();
app.UseSwaggerUI(c =>
{
    c.SwaggerEndpoint("/swagger/v1/swagger.json", "Choice.io API v1");
    c.RoutePrefix = "swagger"; // Доступ по /swagger
});

// Остальное
app.UseCors("AllowAll");
app.UseRouting();
app.MapControllers();

// Console.WriteLine("========================================");
// Console.WriteLine("Choice.io API запущен");
// Console.WriteLine("Доступные эндпоинты:");
// Console.WriteLine("  GET /api/feed?mood=happy&limit=20");
// Console.WriteLine("  GET /api/feed/moods");
// Console.WriteLine("  GET /swagger - документация API");
// Console.WriteLine("========================================");

app.Run();