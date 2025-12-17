using System.Text.Json;
using Api.Models;
using Supabase;
using Supabase.Postgrest;
using Client = Supabase.Client;

namespace Api.Services
{
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
        
                // 1. Получаем ВСЕ настроения и фильтруем в памяти
                var allMoodsResponse = await client
                    .From<SupabaseMood>()
                    .Select("*")
                    .Get();
        
                var mood = allMoodsResponse.Models
                    .FirstOrDefault(m => m.Name.Equals(moodName, StringComparison.OrdinalIgnoreCase));
        
                if (mood == null)
                {
                    Console.WriteLine($"⚠️ Настроение '{moodName}' не найдено");
                    return await GetPopularAnime(client, limit);
                }
        
                Console.WriteLine($"✅ Найдено настроение: {mood.Name} (ID: {mood.Id})");
        
                // 2. Находим жанры, связанные с этим настроением
                var genreMoodsResponse = await client
                    .From<SupabaseMoodGenre>()
                    .Select("*")
                    .Where(gm => gm.MoodId == mood.Id)
                    .Get();
                
                var genreIds = genreMoodsResponse.Models
                    .Select(gm => gm.GenreId)
                    .Distinct()
                    .ToList();
                
                if (!genreIds.Any())
                {
                    Console.WriteLine($"⚠️ Для настроения '{moodName}' не найдены жанры");
                    return await GetPopularAnime(client, limit);
                }
                
                Console.WriteLine($"🎭 Найдено {genreIds.Count} жанров для настроения");
                
                // 3. Находим аниме, связанные с этими жанрами
                // ВАЖНО: Не используем Contains() в Where()!
                // Вместо этого получаем все связи и фильтруем в памяти
                var allAnimeGenresResponse = await client
                    .From<SupabaseAnimeGenre>()
                    .Select("*")
                    .Get();
                
                var animeIds = allAnimeGenresResponse.Models
                    .Where(ag => genreIds.Contains(ag.GenreId))
                    .Select(ag => ag.AnimeId)
                    .Distinct()
                    .ToList();
                
                if (!animeIds.Any())
                {
                    Console.WriteLine($"⚠️ Для жанров настроения '{moodName}' не найдены аниме");
                    return await GetPopularAnime(client, limit);
                }
                
                Console.WriteLine($"📺 Найдено {animeIds.Count} уникальных аниме");
                
                // 4. Получаем детали аниме с сортировкой по рейтингу
                // ВАЖНО: Не используем Contains() в Where()!
                // Вместо этого получаем все аниме и фильтруем в памяти
                var allAnimeResponse = await client
                    .From<SupabaseAnime>()
                    .Select("id, mal_id, url, large_image_url, title, title_english, type, episodes, status, rating, score, synopsis, year")
                    .Order(x => x.Score, Constants.Ordering.Descending)
                    .Get(); // Получаем все аниме с сортировкой
                
                // Фильтруем в памяти по animeIds
                var filteredAnime = allAnimeResponse.Models
                    .Where(a => animeIds.Contains(a.Id))
                    .ToList(); // Убрали Take(limit) здесь
        
                Console.WriteLine($"📊 Доступно {filteredAnime.Count} аниме для настроения '{moodName}'");
        
                // ПЕРЕМЕШИВАЕМ результаты
                var shuffledAnime = filteredAnime
                    .OrderBy(x => Guid.NewGuid()) // или используйте Random
                    .Take(limit)
                    .ToList();
        
                var result = new List<AnimeDto>();
        
                foreach (var anime in shuffledAnime)
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
        
                Console.WriteLine($"✅ Получено {result.Count} аниме для настроения '{moodName}' (перемешано)");
                return result;
            }
            catch (Exception ex)
            {
                Console.WriteLine($"❌ Ошибка при получении аниме по настроению: {ex.Message}");
                Console.WriteLine($"📋 Подробности: {ex}");
                
                // Запасной вариант: получить популярные аниме
                try
                {
                    var client = await _supabaseService.InitSupabase();
                    return await GetPopularAnime(client, limit);
                }
                catch (Exception innerEx)
                {
                    Console.WriteLine($"❌ Ошибка в запасном варианте: {innerEx.Message}");
                    return new List<AnimeDto>();
                }
            }
        }

        // Метод для получения популярных аниме (запасной вариант)
        private async Task<List<AnimeDto>> GetPopularAnime(Client client, int limit)
        {
            Console.WriteLine("🔄 Используем запасной вариант: популярные аниме");
            
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
            
            return result;
        }

        public async Task<List<MoodDto>> GetAllMoods()
        {
            try
            {
                var client = await _supabaseService.InitSupabase();
        
                // Получаем ВСЕ настроения без фильтрации в запросе
                var response = await client
                    .From<SupabaseMood>()
                    .Select("*")
                    .Get();
        
                // Выполняем поиск настроения в памяти
                var allMoods = response.Models;
        
                return allMoods.Select(m => new MoodDto
                {
                    Id = m.Id,
                    Name = m.Name
                }).ToList();
            }
            catch (Exception ex)
            {
                Console.WriteLine($"❌ Ошибка при получении настроений: {ex.Message}");
                return new List<MoodDto>();
            }
        }
    }
}