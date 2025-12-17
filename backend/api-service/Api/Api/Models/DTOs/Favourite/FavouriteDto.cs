namespace Api.Models
{
    public class FavouriteDto
    {
        public long AnimeId { get; set; }
        public int UserId { get; set; }
    }
    
    public class AddFavouriteRequest
    {
        public long AnimeId { get; set; }
        public int UserId { get; set; }
    }
    
    public class RemoveFavouriteRequest
    {
        public long AnimeId { get; set; }
        public int UserId { get; set; }
    }
}