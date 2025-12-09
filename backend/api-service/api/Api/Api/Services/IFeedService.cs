using Api.Models;

namespace Api.Services;

/// <summary>
/// Интерфейс для работы с лентой аниме
/// </summary>
public interface IFeedService
{
    /// <summary>
    /// Получить аниме для указанного настроения
    /// </summary>
    Task<List<AnimeDto>> GetAnimeByMood(string moodName, int limit = 20);
        
    /// <summary>
    /// Получить список всех доступных настроений
    /// </summary>
    Task<List<MoodDto>> GetAllMoods();
}