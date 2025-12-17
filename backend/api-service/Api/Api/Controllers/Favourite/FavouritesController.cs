using Api.Models;
using Api.Services;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Logging;

namespace Api.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class FavouritesController : ControllerBase
    {
        private readonly IFavouriteService _favouriteService;
        private readonly ILogger<FavouritesController> _logger;

        public FavouritesController(IFavouriteService favouriteService, ILogger<FavouritesController> logger)
        {
            _favouriteService = favouriteService;
            _logger = logger;
        }

        [HttpGet]
        public async Task<IActionResult> GetUserFavourites([FromQuery] int userId)
        {
            try
            {
                if (userId <= 0)
                {
                    return BadRequest(new
                    {
                        success = false,
                        error = "Не указан userId"
                    });
                }
                
                _logger.LogInformation($"Запрос избранного для пользователя: {userId}");
                
                var favourites = await _favouriteService.GetUserFavourites(userId);
                
                return Ok(new
                {
                    success = true,
                    userId = userId,
                    count = favourites.Count,
                    animeIds = favourites.Select(f => f.AnimeId)
                });
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Ошибка при получении избранного");
                return StatusCode(500, new
                {
                    success = false,
                    error = "Внутренняя ошибка сервера",
                    details = ex.Message
                });
            }
        }

        [HttpPost]
        public async Task<IActionResult> AddFavourite([FromBody] AddFavouriteRequest request)
        {
            try
            {
                if (request.UserId <= 0)
                {
                    return BadRequest(new
                    {
                        success = false,
                        error = "Не указан userId"
                    });
                }
                
                if (request.AnimeId <= 0)
                {
                    return BadRequest(new
                    {
                        success = false,
                        error = "Неверный ID аниме"
                    });
                }
                
                _logger.LogInformation($"Добавление в избранное: пользователь={request.UserId}, animeId={request.AnimeId}");
                
                var success = await _favouriteService.AddFavourite(request.UserId, request.AnimeId);
                
                if (!success)
                {
                    return Conflict(new
                    {
                        success = false,
                        error = "Аниме уже в избранном"
                    });
                }
                
                return Ok(new
                {
                    success = true,
                    message = "Аниме добавлено в избранное",
                    animeId = request.AnimeId,
                    userId = request.UserId
                });
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Ошибка при добавлении в избранное");
                return StatusCode(500, new
                {
                    success = false,
                    error = "Внутренняя ошибка сервера",
                    details = ex.Message
                });
            }
        }

        [HttpDelete]
        public async Task<IActionResult> RemoveFavourite([FromBody] RemoveFavouriteRequest request)
        {
            try
            {
                if (request.UserId <= 0)
                {
                    return BadRequest(new
                    {
                        success = false,
                        error = "Не указан userId"
                    });
                }
                
                if (request.AnimeId <= 0)
                {
                    return BadRequest(new
                    {
                        success = false,
                        error = "Неверный ID аниме"
                    });
                }
                
                _logger.LogInformation($"Удаление из избранного: пользователь={request.UserId}, animeId={request.AnimeId}");
                
                var success = await _favouriteService.RemoveFavourite(request.UserId, request.AnimeId);
                
                if (!success)
                {
                    return NotFound(new
                    {
                        success = false,
                        error = "Аниме не найдено в избранном"
                    });
                }
                
                return Ok(new
                {
                    success = true,
                    message = "Аниме удалено из избранного",
                    animeId = request.AnimeId,
                    userId = request.UserId
                });
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Ошибка при удалении из избранного");
                return StatusCode(500, new
                {
                    success = false,
                    error = "Внутренняя ошибка сервера",
                    details = ex.Message
                });
            }
        }

        [HttpGet("check")]
        public async Task<IActionResult> CheckFavourite([FromQuery] int userId, [FromQuery] long animeId)
        {
            try
            {
                if (userId <= 0)
                {
                    return BadRequest(new
                    {
                        success = false,
                        error = "Не указан userId"
                    });
                }
                
                if (animeId <= 0)
                {
                    return BadRequest(new
                    {
                        success = false,
                        error = "Неверный ID аниме"
                    });
                }
                
                _logger.LogInformation($"Проверка избранного: пользователь={userId}, animeId={animeId}");
                
                var isFavourite = await _favouriteService.IsAnimeFavourite(userId, animeId);
                
                return Ok(new
                {
                    success = true,
                    isFavourite = isFavourite,
                    animeId = animeId,
                    userId = userId
                });
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Ошибка при проверке избранного");
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