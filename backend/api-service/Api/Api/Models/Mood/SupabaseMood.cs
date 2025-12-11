using Supabase.Postgrest.Attributes;
using Supabase.Postgrest.Models;

namespace Api.Models;

/// <summary>
/// Модель для таблицы moods в Supabase
/// </summary>
[Table("moods")]
public class SupabaseMood : BaseModel
{
    [PrimaryKey("id")]
    public int Id { get; set; }
        
    [Column("name")]
    public string Name { get; set; } = string.Empty;
}