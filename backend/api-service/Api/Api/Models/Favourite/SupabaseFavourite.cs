using Supabase.Postgrest.Attributes;
using Supabase.Postgrest.Models;

namespace Api.Models
{
    [Table("favourites")]
    public class SupabaseFavourite : BaseModel
    {
        [Column("anime_id")]
        public long AnimeId { get; set; }
        
        [Column("user_id")]
        public int UserId { get; set; }
    }
}