using System.Text.Json;
using Api.Models;
using Supabase;
using Supabase.Postgrest;

namespace Api.Services;
public class FeedService : IFeedService
    {
        private readonly ISupabaseService _supabaseService;

        public FeedService(ISupabaseService supabaseService)
        {
            _supabaseService = supabaseService;
        }

        public async Task<List<AnimeDto>> GetAnimeByMood(string moodName, int limit = 20)
        {
            Console.WriteLine($"🎭 Получение ленты: mood={moodName}, limit={limit}");
            
            try
            {
                var client = await _supabaseService.InitSupabase();
                
                // Просто получаем аниме из базы
                var response = await client
                    .From<SupabaseAnime>()
                    .Select("id, mal_id, url, large_image_url, title, title_english, type, episodes, status, rating, score, synopsis, year")
                    .Order(x => x.Score, Constants.Ordering.Descending)
                    .Limit(limit)
                    .Get();
                
                var result = new List<AnimeDto>();
                
                foreach (var anime in response.Models)
                {
                    result.Add(new AnimeDto
                    {
                        Id = anime.Id,
                        MalId = anime.MalId,
                        Url = anime.Url,
                        LargeImageUrl = anime.LargeImageUrl,
                        Title = anime.Title,
                        TitleEnglish = anime.TitleEnglish,
                        Type = anime.Type,
                        Episodes = anime.Episodes,
                        Status = anime.Status,
                        Rating = anime.Rating,
                        Score = anime.Score,
                        Synopsis = anime.Synopsis,
                        Year = anime.Year
                    });
                }
                
                Console.WriteLine($"✅ Найдено {result.Count} аниме");
                return result;
            }
            catch (Exception ex)
            {
                Console.WriteLine($"❌ Ошибка: {ex.Message}");
                return new List<AnimeDto>();
            }
        }

        public async Task<List<MoodDto>> GetAllMoods()
        {
            try
            {
                var client = await _supabaseService.InitSupabase();
                
                var response = await client
                    .From<SupabaseMood>()
                    .Select("*")
                    .Get();
                
                return response.Models.Select(m => new MoodDto
                {
                    Id = m.Id,
                    Name = m.Name
                }).ToList();
            }
            catch (Exception ex)
            {
                Console.WriteLine($"❌ Ошибка: {ex.Message}");
                return new List<MoodDto>();
            }
        }
    }