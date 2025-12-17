using Api.Models;
using Newtonsoft.Json;

namespace Api.Services
{
    public class AuthService : IAuthService
    {
        private readonly ISupabaseService _supabaseService;

        public AuthService(ISupabaseService supabaseService)
        {
            _supabaseService = supabaseService;
        }

        public async Task<AuthResponseDto> Register(AuthRequestDto request)
        {
            try
            {
                Console.WriteLine($"Регистрация пользователя: {request.Name}");
                
                // Проверяем, существует ли пользователь
                var exists = await UserExists(request.Name);
                if (exists)
                {
                    return new AuthResponseDto
                    {
                        Success = false,
                        Message = "Пользователь с таким именем уже существует"
                    };
                }
                
                var client = await _supabaseService.InitSupabase();
                
                // Создаем нового пользователя
                var newUser = new SupabaseUser
                {
                    Name = request.Name,
                    Password = request.Password // Храним как есть (по твоему требованию)
                };
                
                var response = await client
                    .From<SupabaseUser>()
                    .Insert(newUser);
                
                if (response.Models.Any())
                {
                    var user = response.Models.First();
                    
                    return new AuthResponseDto
                    {
                        Success = true,
                        Message = "Регистрация успешна",
                        UserId = user.Id
                    };
                }
                
                return new AuthResponseDto
                {
                    Success = false,
                    Message = "Ошибка при создании пользователя"
                };
            }
            catch (Exception ex)
            {
                Console.WriteLine($"Ошибка регистрации: {ex.Message}");
                return new AuthResponseDto
                {
                    Success = false,
                    Message = $"Ошибка сервера: {ex.Message}"
                };
            }
        }

        public async Task<AuthResponseDto> Login(AuthRequestDto request)
        {
            try
            {
                Console.WriteLine($"Вход пользователя: {request.Name}");
                
                var client = await _supabaseService.InitSupabase();
                
                // Ищем пользователя по имени
                var response = await client
                    .From<SupabaseUser>()
                    .Where(u => u.Name == request.Name)
                    .Get();
                
                var user = response.Models.FirstOrDefault();
                
                if (user == null)
                {
                    return new AuthResponseDto
                    {
                        Success = false,
                        Message = "Пользователь не найден"
                    };
                }
                
                // Проверяем пароль (простое сравнение)
                if (user.Password != request.Password)
                {
                    return new AuthResponseDto
                    {
                        Success = false,
                        Message = "Неверный пароль"
                    };
                }
                
                return new AuthResponseDto
                {
                    Success = true,
                    Message = "Вход успешен",
                    UserId = user.Id
                };
            }
            catch (Exception ex)
            {
                Console.WriteLine($"Ошибка входа: {ex.Message}");
                return new AuthResponseDto
                {
                    Success = false,
                    Message = $"Ошибка сервера: {ex.Message}"
                };
            }
        }

        public async Task<bool> UserExists(string name)
        {
            try
            {
                var client = await _supabaseService.InitSupabase();
                
                var response = await client
                    .From<SupabaseUser>()
                    .Where(u => u.Name == name)
                    .Get();
                
                return response.Models.Any();
            }
            catch
            {
                return false;
            }
        }
    }
}