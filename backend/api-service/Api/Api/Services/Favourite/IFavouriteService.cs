using Api.Models;

namespace Api.Services
{
    public interface IFavouriteService
    {
        Task<List<FavouriteDto>> GetUserFavourites(int userId);
        Task<bool> AddFavourite(int userId, long animeId);
        Task<bool> RemoveFavourite(int userId, long animeId);
        Task<bool> IsAnimeFavourite(int userId, long animeId);
    }
}