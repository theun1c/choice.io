using System.Text.Json;

namespace Api.Models;

public class AnimeDto
{
    public long Id { get; set; }
    public int MalId { get; set; }
    public string Url { get; set; } = string.Empty;
    public string? LargeImageUrl { get; set; } // Просто строка URL
    public string Title { get; set; } = string.Empty;
    public string? TitleEnglish { get; set; }
    public string? Type { get; set; }
    public int? Episodes { get; set; }
    public string? Status { get; set; }
    public string? Rating { get; set; }
    public double? Score { get; set; }
    public string? Synopsis { get; set; }
    public int? Year { get; set; }
}