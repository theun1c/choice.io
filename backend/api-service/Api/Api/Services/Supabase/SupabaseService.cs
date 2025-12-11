using Supabase;

namespace Api.Services;

/// <summary>
/// Сервис для подключения к базе данных Supabase
/// </summary>
public class SupabaseService : ISupabaseService
{
    private readonly IConfiguration _configuration;
    private string _supabaseUrl;
    private string _supabaseKey;
    private Client? _supabaseClient;

    public SupabaseService(IConfiguration configuration)
    {
        _configuration = configuration;
        _supabaseUrl = _configuration["Supabase:Url"] ?? 
                       throw new ArgumentException("Supabase:Url не настроен в appsettings.json");
        _supabaseKey = _configuration["Supabase:Key"] ?? 
                       throw new ArgumentException("Supabase:Key не настроен в appsettings.json");
    }

    public async Task<Client> InitSupabase()
    {
        if (_supabaseClient != null)
            return _supabaseClient;

        var options = new SupabaseOptions
        {
            AutoConnectRealtime = false,
            AutoRefreshToken = false,
        };

        _supabaseClient = new Client(_supabaseUrl, _supabaseKey, options);
        await _supabaseClient.InitializeAsync();

        Console.WriteLine($"Supabase клиент инициализирован: {_supabaseUrl}");
        return _supabaseClient;
    }
}
