using Supabase;

namespace Api.Services;
public interface ISupabaseService { Task<Client> InitSupabase(); }
