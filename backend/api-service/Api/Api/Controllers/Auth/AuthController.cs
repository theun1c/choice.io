using Api.Models;
using Api.Services;
using Microsoft.AspNetCore.Mvc;

namespace Api.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class AuthController : ControllerBase
    {
        private readonly IAuthService _authService;

        public AuthController(IAuthService authService)
        {
            _authService = authService;
        }

        /// <summary>
        /// Регистрация нового пользователя
        /// POST /api/auth/register
        /// </summary>
        [HttpPost("register")]
        public async Task<IActionResult> Register([FromBody] AuthRequestDto request)
        {
            try
            {
                Console.WriteLine($"Запрос на регистрацию: {request.Name}");
                
                // Валидация
                if (string.IsNullOrWhiteSpace(request.Name) || string.IsNullOrWhiteSpace(request.Password))
                {
                    return BadRequest(new { success = false, message = "Имя и пароль обязательны" });
                }
                
                if (request.Name.Length < 3)
                {
                    return BadRequest(new { success = false, message = "Имя должно быть не менее 3 символов" });
                }
                
                if (request.Password.Length < 4)
                {
                    return BadRequest(new { success = false, message = "Пароль должен быть не менее 4 символов" });
                }
                
                // Регистрация
                var result = await _authService.Register(request);
                
                if (result.Success)
                {
                    return Ok(new
                    {
                        success = true,
                        message = result.Message,
                        userId = result.UserId
                    });
                }
                
                return BadRequest(new
                {
                    success = false,
                    message = result.Message
                });
            }
            catch (Exception ex)
            {
                Console.WriteLine($"Ошибка в Register: {ex.Message}");
                return StatusCode(500, new
                {
                    success = false,
                    message = "Внутренняя ошибка сервера"
                });
            }
        }

        /// <summary>
        /// Вход пользователя
        /// POST /api/auth/login
        /// </summary>
        [HttpPost("login")]
        public async Task<IActionResult> Login([FromBody] AuthRequestDto request)
        {
            try
            {
                Console.WriteLine($"Запрос на вход: {request.Name}");
                
                // Валидация
                if (string.IsNullOrWhiteSpace(request.Name) || string.IsNullOrWhiteSpace(request.Password))
                {
                    return BadRequest(new { success = false, message = "Имя и пароль обязательны" });
                }
                
                // Вход
                var result = await _authService.Login(request);
                
                if (result.Success)
                {
                    return Ok(new
                    {
                        success = true,
                        message = result.Message,
                        userId = result.UserId
                    });
                }
                
                return Unauthorized(new
                {
                    success = false,
                    message = result.Message
                });
            }
            catch (Exception ex)
            {
                Console.WriteLine($"Ошибка в Login: {ex.Message}");
                return StatusCode(500, new
                {
                    success = false,
                    message = "Внутренняя ошибка сервера"
                });
            }
        }

        /// <summary>
        /// Проверка существования пользователя
        /// GET /api/auth/check?name=username
        /// </summary>
        [HttpGet("check")]
        public async Task<IActionResult> CheckUser([FromQuery] string name)
        {
            try
            {
                if (string.IsNullOrWhiteSpace(name))
                {
                    return BadRequest(new { exists = false, message = "Укажите имя" });
                }
                
                var exists = await _authService.UserExists(name);
                
                return Ok(new
                {
                    exists = exists,
                    message = exists ? "Пользователь существует" : "Пользователь не найден"
                });
            }
            catch (Exception ex)
            {
                Console.WriteLine($"Ошибка в CheckUser: {ex.Message}");
                return StatusCode(500, new
                {
                    exists = false,
                    message = "Ошибка сервера"
                });
            }
        }
    }
}