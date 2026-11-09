// ScholarConnect - API Base Configuration
const Config = {
    // If frontend is served directly by Spring Boot on 8080 or deployed, use relative/dynamic origin
    API_BASE_URL: (function() {
        if (window.location.port === '8080' || window.location.hostname !== 'localhost') {
            return window.location.origin + '/api';
        }
        // When running standalone on another port (e.g. 5500, 3000, or file://)
        return 'http://localhost:8080/api';
    })(),

    STORAGE_KEY_TOKEN: 'scholarconnect_token',
    STORAGE_KEY_USER: 'scholarconnect_user'
};

window.Config = Config;
