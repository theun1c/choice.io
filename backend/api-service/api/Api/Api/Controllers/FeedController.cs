using Api.Models;
using Api.Services;
using Microsoft.AspNetCore.Mvc;

namespace Api.Controllers;
[ApiController]
    [Route("api/[controller]")]
    public class FeedController : ControllerBase
    {
        private readonly IFeedService _feedService;

        public FeedController(IFeedService feedService)
        {
            _feedService = feedService;
        }

        /// <summary>
        /// Получить ленту аниме по настроению
        /// Пример: GET /api/feed?mood=happy&limit=20
        /// </summary>
        [HttpGet]
        public async Task<IActionResult> GetFeed(
            [FromQuery] string mood = "happy",
            [FromQuery] int limit = 20)
        {
            try
            {
                Console.WriteLine($"Запрос ленты: mood={mood}, limit={limit}");
                
                // Простая валидация
                if (limit > 50) limit = 50;
                if (limit < 1) limit = 1;
                
                // Получаем аниме
                var animeList = await _feedService.GetAnimeByMood(mood, limit);
                
                // Простой ответ
                return Ok(new
                {
                    success = true,
                    mood = mood,
                    count = animeList.Count,
                    anime = animeList
                });
            }
            catch (Exception ex)
            {
                Console.WriteLine($"Ошибка: {ex.Message}");
                return StatusCode(500, new
                {
                    success = false,
                    error = ex.Message
                });
            }
        }

        /// <summary>
        /// Получить список настроений
        /// Пример: GET /api/feed/moods
        /// </summary>
        [HttpGet("moods")]
        public async Task<IActionResult> GetMoods()
        {
            try
            {
                Console.WriteLine("Запрос списка настроений");
                
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
                Console.WriteLine($"Ошибка: {ex.Message}");
                return StatusCode(500, new
                {
                    success = false,
                    error = ex.Message
                });
            }
        }
    }