using Api.Models;


namespace Api.Services
{
    public interface IAuthService
    {
        Task<AuthResponseDto> Register(AuthRequestDto request);
        Task<AuthResponseDto> Login(AuthRequestDto request);
        Task<bool> UserExists(string name);
    }
}