using System.Text.Json;
using Supabase.Postgrest.Attributes;
using Supabase.Postgrest.Models;

namespace Api.Models
{
    [Table("anime")]
    public class SupabaseAnime : BaseModel
    {
        [PrimaryKey("id")]
        public long Id { get; set; }
        
        [Column("mal_id")]
        public int MalId { get; set; }
        
        [Column("url")]
        public string Url { get; set; } = string.Empty;
        
        // Старое поле (можете удалить из выборки)
        [Column("images")]
        public string? Images { get; set; }
        
        // НОВОЕ поле - прямая ссылка на картинку
        [Column("large_image_url")]
        public string? LargeImageUrl { get; set; }
        
        [Column("title")]
        public string Title { get; set; } = string.Empty;
        
        [Column("title_english")]
        public string? TitleEnglish { get; set; }
        
        [Column("type")]
        public string? Type { get; set; }
        
        [Column("episodes")]
        public int? Episodes { get; set; }
        
        [Column("status")]
        public string? Status { get; set; }
        
        [Column("rating")]
        public string? Rating { get; set; }
        
        [Column("score")]
        public double? Score { get; set; }
        
        [Column("synopsis")]
        public string? Synopsis { get; set; }
        
        [Column("year")]
        public int? Year { get; set; }
    }
}