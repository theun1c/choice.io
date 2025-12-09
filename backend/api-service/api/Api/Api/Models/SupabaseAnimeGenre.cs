using Supabase.Postgrest.Attributes;
using Supabase.Postgrest.Models;

namespace Api.Models;

/// <summary>
/// Модель для таблицы anime_genres в Supabase
/// </summary>
[Table("anime_genres")]
public class SupabaseAnimeGenre : BaseModel
{
    [Column("anime_id")]
    public long AnimeId { get; set; }
        
    [Column("genre_id")]
    public long GenreId { get; set; }
}