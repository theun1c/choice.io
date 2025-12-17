using Api.Models;
using Api.Services;
using Microsoft.AspNetCore.Mvc;

namespace Api.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class FeedController : ControllerBase
    {
        private readonly IFeedService _feedService;
        private readonly ILogger<FeedController> _logger;

        public FeedController(IFeedService feedService, ILogger<FeedController> logger)
        {
            _feedService = feedService;
            _logger = logger;
        }

        [HttpGet]
        public async Task<IActionResult> GetFeed(
            [FromQuery] string mood = "happy",
            [FromQuery] int limit = 20)
        {
            try
            {
                _logger.LogInformation($"Запрос ленты: mood={mood}, limit={limit}");
                
                // Валидация параметров
                if (string.IsNullOrWhiteSpace(mood))
                {
                    return BadRequest(new
                    {
                        success = false,
                        error = "Настроение не может быть пустым"
                    });
                }
                
                if (limit > 50) limit = 50;
                if (limit < 1) limit = 1;
                
                // Получаем доступные настроения для проверки
                var allMoods = await _feedService.GetAllMoods();
                var validMoodNames = allMoods.Select(m => m.Name.ToLower()).ToList();
                
                if (!validMoodNames.Contains(mood.ToLower()))
                {
                    return BadRequest(new
                    {
                        success = false,
                        error = $"Неизвестное настроение '{mood}'. Доступные: {string.Join(", ", validMoodNames)}",
                        availableMoods = validMoodNames
                    });
                }
                
                // Получаем аниме
                var animeList = await _feedService.GetAnimeByMood(mood, limit);
                
                return Ok(new
                {
                    success = true,
                    mood = mood,
                    requestedLimit = limit,
                    actualCount = animeList.Count,
                    anime = animeList
                });
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, $"Ошибка при обработке запроса ленты: mood={mood}");
                return StatusCode(500, new
                {
                    success = false,
                    error = "Внутренняя ошибка сервера",
                    details = ex.Message
                });
            }
        }

        [HttpGet("moods")]
        public async Task<IActionResult> GetMoods()
        {
            try
            {
                _logger.LogInformation("Запрос списка настроений");
                
                var moods = await _feedService.GetAllMoods();
                
                return Ok(new
                {
                    success = true,
                    count = moods.Count,
                    moods = moods
                });
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Ошибка при получении списка настроений");
                return StatusCode(500, new
                {
                    success = false,
                    error = "Внутренняя ошибка сервера",
                    details = ex.Message
                });
            }
        }
    }
}