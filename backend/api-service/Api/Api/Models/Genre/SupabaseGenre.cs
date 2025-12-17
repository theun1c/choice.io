using Supabase.Postgrest.Attributes;
using Supabase.Postgrest.Models;

namespace Api.Models
{
    [Table("genres")]
    public class SupabaseGenre : BaseModel
    {
        [PrimaryKey("id")]
        public long Id { get; set; }
        
        [Column("mal_id")]
        public int? MalId { get; set; }
        
        [Column("type")]
        public string? Type { get; set; }
        
        [Column("name")]
        public string Name { get; set; } = string.Empty;
        
        [Column("url")]
        public string? Url { get; set; }
    }
}