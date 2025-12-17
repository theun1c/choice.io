using Api.Models;
using Supabase;
using Supabase.Postgrest;
using Microsoft.Extensions.Logging;

namespace Api.Services
{
    public class FavouriteService : IFavouriteService
    {
        private readonly ISupabaseService _supabaseService;
        private readonly ILogger<FavouriteService> _logger;

        public FavouriteService(ISupabaseService supabaseService, ILogger<FavouriteService> logger)
        {
            _supabaseService = supabaseService;
            _logger = logger;
        }

        public async Task<List<FavouriteDto>> GetUserFavourites(int userId)
        {
            try
            {
                _logger.LogInformation($"Получение избранного для пользователя: {userId}");
                
                var client = await _supabaseService.InitSupabase();
                
                var response = await client
                    .From<SupabaseFavourite>()
                    .Select("*")
                    .Where(f => f.UserId == userId)
                    .Get();
                
                var favourites = response.Models.Select(f => new FavouriteDto
                {
                    AnimeId = f.AnimeId,
                    UserId = f.UserId
                }).ToList();
                
                _logger.LogInformation($"Найдено {favourites.Count} избранных аниме для пользователя {userId}");
                return favourites;
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, $"Ошибка при получении избранного для пользователя {userId}");
                throw;
            }
        }

        public async Task<bool> AddFavourite(int userId, long animeId)
        {
            try
            {
                _logger.LogInformation($"Добавление в избранное: пользователь={userId}, animeId={animeId}");
                
                if (await IsAnimeFavourite(userId, animeId))
                {
                    _logger.LogWarning($"Аниме {animeId} уже в избранном у пользователя {userId}");
                    return false;
                }
                
                var client = await _supabaseService.InitSupabase();
                
                var favourite = new SupabaseFavourite
                {
                    AnimeId = animeId,
                    UserId = userId
                };
                
                var response = await client
                    .From<SupabaseFavourite>()
                    .Insert(favourite);
                
                var success = response.Models.Count > 0;
                
                if (success)
                {
                    _logger.LogInformation($"Успешно добавлено в избранное: пользователь={userId}, animeId={animeId}");
                }
                
                return success;
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, $"Ошибка при добавлении в избранное: пользователь={userId}, animeId={animeId}");
                return false;
            }
        }

        public async Task<bool> RemoveFavourite(int userId, long animeId)
        {
            try
            {
                _logger.LogInformation($"Удаление из избранного: пользователь={userId}, animeId={animeId}");
        
                var client = await _supabaseService.InitSupabase();
        
                // Проверяем, существует ли запись
                var checkResponse = await client
                    .From<SupabaseFavourite>()
                    .Where(f => f.UserId == userId && f.AnimeId == animeId)
                    .Get();
        
                if (checkResponse.Models.Count == 0)
                {
                    _logger.LogWarning($"Запись для удаления не найдена: пользователь={userId}, animeId={animeId}");
                    return false;
                }
        
                // Выполняем удаление (не присваиваем результат)
                await client
                    .From<SupabaseFavourite>()
                    .Where(f => f.UserId == userId && f.AnimeId == animeId)
                    .Delete();
        
                // Проверяем, действительно ли удалилось
                var verifyResponse = await client
                    .From<SupabaseFavourite>()
                    .Where(f => f.UserId == userId && f.AnimeId == animeId)
                    .Get();
        
                var success = verifyResponse.Models.Count == 0;
        
                if (success)
                {
                    _logger.LogInformation($"Успешно удалено из избранного: пользователь={userId}, animeId={animeId}");
                }
                else
                {
                    _logger.LogWarning($"Не удалось удалить из избранного: пользователь={userId}, animeId={animeId}");
                }
        
                return success;
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, $"Ошибка при удалении из избранного: пользователь={userId}, animeId={animeId}");
                return false;
            }
        }

        public async Task<bool> IsAnimeFavourite(int userId, long animeId)
        {
            try
            {
                var client = await _supabaseService.InitSupabase();
                
                var response = await client
                    .From<SupabaseFavourite>()
                    .Where(f => f.UserId == userId && f.AnimeId == animeId)
                    .Get();
                
                return response.Models.Count > 0;
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, $"Ошибка при проверке избранного: пользователь={userId}, animeId={animeId}");
                return false;
            }
        }
    }
}