package services

import (
	"bytes"
	"encoding/json"
	"fmt"
	"io"
	"net/http"
	"os"
	"time"

	"github.com/darenliang/jikan-go"
	"github.com/joho/godotenv"
)

// структура лоадера
type Unloader struct {
}

// конструктор лоадера
func NewUnloader() *Unloader {
	return &Unloader{}
}

// Жанры без аниме
type Genre struct {
	ID    int64  `json:"id,omitempty"`
	MalId int    `json:"mal_id"`
	Type  string `json:"type"`
	Name  string `json:"name"`
	Url   string `json:"url"`
}

// Структура для необходимых данных
type Anime struct {
	ID           int64         `json:"id,omitempty"`
	MalId        int           `json:"mal_id"`
	Url          string        `json:"url"`
	Images       jikan.Images3 `json:"images"`
	Title        string        `json:"title"`
	TitleEnglish string        `json:"title_english"`
	Type         string        `json:"type"`
	Episodes     int           `json:"episodes"`
	Status       string        `json:"status"`
	Rating       string        `json:"rating"`
	Score        float64       `json:"score"`
	Synopsis     string        `json:"synopsis"`
	Year         int           `json:"year"`
}

// Таблица связи многие-ко-многим
type AnimeGenre struct {
	AnimeID int64 `json:"anime_id"`
	GenreID int64 `json:"genre_id"`
}

// TODO: разбить объект Anime на несколько подъобъектов для удобной записи в супабейз

// получили поля Аниме. Получили поля жанров.
// жанры будут записываться в БД и проверяться на новые поля.
// аниме сущность будет хранить массив айдишников на жанры, что создает связь 1 ко мн
func (u *Unloader) Start() {
	// в этом месте получаем только 25 записей за 1 запрос.
	// нужно будет увеличить таймаут и итерировать страницы
	anime, err := jikan.GetTopAnime("tv", "bypopularity", 2)
	if err != nil {
		fmt.Println("error: ", err)
		return
	}

	// запускаем все в цикле
	for i := 0; i < len(anime.Data); i++ {
		// TODO: так может не делать общую структуру а разбить по мелким объектам сразу ?
		// А нужно ли вообще разбивать все на разные таблицы ?

		// пришел к выводу о том, что ТИПЫ аниме выделять в отдельную таблицу не стоит
		// поскольку типы не нужны в аналитике и в данном проекте. они являются просто текстовым полем - не более
		// и влияют только на описание, в то время как ЖАНРЫ, которые следует выделить в отдельную таблицу,
		// помогут в реализации основной задумки проекта
		animeItem := Anime{
			MalId:        anime.Data[i].MalId,
			Url:          anime.Data[i].Url,
			Images:       anime.Data[i].Images,
			Title:        anime.Data[i].Title,
			TitleEnglish: anime.Data[i].TitleEnglish,
			Type:         anime.Data[i].Type,
			Episodes:     anime.Data[i].Episodes,
			Status:       anime.Data[i].Status,
			Rating:       anime.Data[i].Rating,
			Score:        anime.Data[i].Score,
			Synopsis:     anime.Data[i].Synopsis,
			Year:         anime.Data[i].Year,
		}

		insertedAnime, err := insertAnime(animeItem)
		if err != nil {
			fmt.Println("Failed to insert anime row")
			return
		}

		for j := 0; j < len(anime.Data[i].Genres); j++ {
			genreItem := Genre{
				MalId: anime.Data[i].Genres[j].MalId,
				Type:  anime.Data[i].Genres[j].Type,
				Name:  anime.Data[i].Genres[j].Name,
				Url:   anime.Data[i].Genres[j].Url,
			}

			insertedGenre, err := insertGenre(genreItem)
			if err != nil {
				fmt.Println("Failed to insert genre")
				return
			}

			err = insertAnimeGenre(insertedAnime.ID, insertedGenre.ID)
			if err != nil {
				fmt.Printf("Failed to create relation %v", err)
			}
		}

	}
}

// // для удаления повторяющихся жанров
// func removeDup(inputSlice []Genre) []Genre {
// 	isUnique := map[Genre]bool{}

// 	resultSlice := []Genre{}

// 	for _, item := range inputSlice {
// 		if !isUnique[item] {
// 			isUnique[item] = true
// 			resultSlice = append(resultSlice, item)
// 		}
// 	}

// 	return resultSlice
// }

func insertAnime(animeItem Anime) (*Anime, error) {

	err := godotenv.Load()
	if err != nil {
		fmt.Println("Warning: Could not load .env file")
	}

	supabaseKey := os.Getenv("API_KEY")
	supabaseURL := os.Getenv("API_URL")

	fmt.Printf("API_URL: %s\n", supabaseURL)
	fmt.Printf("API_KEY length: %d\n", len(supabaseKey))

	if supabaseURL == "" {
		return nil, fmt.Errorf("SUPABASE_URL is empty")
	}
	if supabaseKey == "" {
		return nil, fmt.Errorf("SUPABASE_KEY is empty")
	}

	jsonData, err := json.Marshal(animeItem)
	if err != nil {
		return nil, fmt.Errorf("marshal error: %w", err)
	}

	url := fmt.Sprintf("%s/rest/v1/anime", supabaseURL)
	fmt.Printf("Full URL: %s\n", url) // ← посмотрим полный URL

	req, err := http.NewRequest("POST", url, bytes.NewBuffer(jsonData))
	if err != nil {
		return nil, fmt.Errorf("request creation error: %w", err)
	}

	req.Header.Set("Content-Type", "application/json")
	req.Header.Set("apikey", supabaseKey)
	req.Header.Set("Authorization", "Bearer "+supabaseKey)
	req.Header.Set("Prefer", "return=representation")

	// Добавляем таймаут
	client := &http.Client{
		Timeout: 30 * time.Second,
	}

	fmt.Println("Sending request...")
	resp, err := client.Do(req)
	if err != nil {
		return nil, fmt.Errorf("HTTP request failed: %w", err)
	}

	defer resp.Body.Close()

	body, _ := io.ReadAll(resp.Body)
	fmt.Printf("Response status: %d\n", resp.StatusCode)
	fmt.Printf("Response body: %s\n", string(body))

	if resp.StatusCode != 201 {
		return nil, fmt.Errorf("HTTP %d: %s", resp.StatusCode, resp.Status)
	}

	var result Anime
	err = json.Unmarshal(body, &result)
	if err != nil {
		return nil, fmt.Errorf("decode error: %w", err)
	}

	return &result, nil
}

func insertGenre(genreItem Genre) (*Genre, error) {
	err := godotenv.Load()
	if err != nil {
		fmt.Println("Warning: Could not load .env file")
	}

	exists, genreID, err := genreExists(genreItem.MalId)
	if err != nil {
		return nil, err
	}

	if exists {
		// Если существует - возвращаем его
		return &Genre{ID: genreID, MalId: genreItem.MalId, Name: genreItem.Name, Type: genreItem.Type, Url: genreItem.Url}, nil
	}

	supabaseKey := os.Getenv("API_KEY")
	supabaseURL := os.Getenv("API_URL")

	fmt.Printf("API_URL: %s\n", supabaseURL)
	fmt.Printf("API_KEY length: %d\n", len(supabaseKey))

	if supabaseURL == "" {
		return nil, fmt.Errorf("SUPABASE_URL is empty")
	}
	if supabaseKey == "" {
		return nil, fmt.Errorf("SUPABASE_KEY is empty")
	}

	jsonData, err := json.Marshal(genreItem)
	if err != nil {
		return nil, fmt.Errorf("marshal error: %w", err)
	}

	url := fmt.Sprintf("%s/rest/v1/genre", supabaseURL)
	fmt.Printf("Full URL: %s\n", url) // ← посмотрим полный URL

	req, err := http.NewRequest("POST", url, bytes.NewBuffer(jsonData))
	if err != nil {
		return nil, fmt.Errorf("request creation error: %w", err)
	}

	req.Header.Set("Content-Type", "application/json")
	req.Header.Set("apikey", supabaseKey)
	req.Header.Set("Authorization", "Bearer "+supabaseKey)
	req.Header.Set("Prefer", "return=representation")

	// Добавляем таймаут
	client := &http.Client{
		Timeout: 30 * time.Second,
	}

	fmt.Println("Sending request...")
	resp, err := client.Do(req)
	if err != nil {
		return nil, fmt.Errorf("HTTP request failed: %w", err)
	}

	defer resp.Body.Close()

	body, _ := io.ReadAll(resp.Body)
	fmt.Printf("Response status: %d\n", resp.StatusCode)
	fmt.Printf("Response body: %s\n", string(body))

	if resp.StatusCode != 201 {
		return nil, fmt.Errorf("HTTP %d: %s", resp.StatusCode, resp.Status)
	}

	var result Genre
	err = json.Unmarshal(body, &result)
	if err != nil {
		return nil, fmt.Errorf("decode error: %w", err)
	}

	return &result, nil
}

func genreExists(malId int) (bool, int64, error) {
	supabaseKey := os.Getenv("API_KEY")
	supabaseURL := os.Getenv("API_URL")

	url := fmt.Sprintf("%s/rest/v1/anime?mal_id=eq.%d&select=id", supabaseURL, malId)
	req, err := http.NewRequest("GET", url, nil)
	if err != nil {
		return false, 0, err
	}

	req.Header.Set("apikey", supabaseKey)
	req.Header.Set("Authorization", "Bearer "+supabaseKey)

	client := &http.Client{}
	resp, err := client.Do(req)
	if err != nil {
		return false, 0, err
	}
	defer resp.Body.Close()

	if resp.StatusCode != 200 {
		return false, 0, fmt.Errorf("HTTP %d", resp.StatusCode)
	}

	var result []struct {
		ID int64 `json:"id"`
	}

	err = json.NewDecoder(resp.Body).Decode(&result)
	if err != nil {
		return false, 0, err
	}

	if len(result) > 0 {
		return true, result[0].ID, nil
	}

	return false, 0, nil
}

func insertAnimeGenre(animeID, genreID int64) error {

	err := godotenv.Load()
	if err != nil {
		fmt.Println("Warning: Could not load .env file")
	}

	supabaseKey := os.Getenv("API_KEY")
	supabaseURL := os.Getenv("API_URL")

	fmt.Printf("API_URL: %s\n", supabaseURL)
	fmt.Printf("API_KEY length: %d\n", len(supabaseKey))

	if supabaseURL == "" {
		return fmt.Errorf("SUPABASE_URL is empty")
	}
	if supabaseKey == "" {
		return fmt.Errorf("SUPABASE_KEY is empty")
	}

	animeGenreItem := AnimeGenre{
		AnimeID: animeID,
		GenreID: genreID,
	}

	jsonData, err := json.Marshal(animeGenreItem)
	if err != nil {
		return fmt.Errorf("marshal error: %w", err)
	}

	url := fmt.Sprintf("%s/rest/v1/anime_genre", supabaseURL)
	fmt.Printf("Full URL: %s\n", url) // ← посмотрим полный URL

	req, err := http.NewRequest("POST", url, bytes.NewBuffer(jsonData))
	if err != nil {
		return fmt.Errorf("request creation error: %w", err)
	}

	req.Header.Set("Content-Type", "application/json")
	req.Header.Set("apikey", supabaseKey)
	req.Header.Set("Authorization", "Bearer "+supabaseKey)
	req.Header.Set("Prefer", "return=representation")

	// Добавляем таймаут
	client := &http.Client{
		Timeout: 30 * time.Second,
	}

	fmt.Println("Sending request...")
	resp, err := client.Do(req)
	if err != nil {
		return fmt.Errorf("HTTP request failed: %w", err)
	}

	defer resp.Body.Close()

	body, _ := io.ReadAll(resp.Body)
	fmt.Printf("Response status: %d\n", resp.StatusCode)
	fmt.Printf("Response body: %s\n", string(body))

	if resp.StatusCode != 201 {
		return fmt.Errorf("HTTP %d: %s", resp.StatusCode, resp.Status)
	}

	var result AnimeGenre
	err = json.Unmarshal(body, &result)
	if err != nil {
		return fmt.Errorf("decode error: %w", err)
	}

	return nil
}
