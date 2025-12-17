using Supabase.Postgrest.Attributes;
using Supabase.Postgrest.Models;

namespace Api.Models;

/// <summary>
/// Модель для таблицы mood_genres в Supabase
/// </summary>
[Table("mood_genres")]
public class SupabaseMoodGenre : BaseModel
{
    [Column("mood_id")]
    public int MoodId { get; set; }
        
    [Column("genre_id")]
    public long GenreId { get; set; }
}