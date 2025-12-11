using Supabase.Postgrest.Attributes;
using Supabase.Postgrest.Models;

namespace Api.Models
{
    [Table("users")]
    public class SupabaseUser : BaseModel
    {
        [PrimaryKey("id", false)]
        public string Id { get; set; }
        
        [Column("name")]
        public string Name { get; set; } = string.Empty;
        
        [Column("password")]
        public string Password { get; set; } = string.Empty;
        
    }
}