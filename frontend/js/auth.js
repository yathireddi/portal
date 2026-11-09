// ScholarConnect - Authentication Manager
const Auth = {
    getToken() {
        return localStorage.getItem(Config.STORAGE_KEY_TOKEN) || sessionStorage.getItem(Config.STORAGE_KEY_TOKEN);
    },

    getUser() {
        const u = localStorage.getItem(Config.STORAGE_KEY_USER) || sessionStorage.getItem(Config.STORAGE_KEY_USER);
        try {
            return u ? JSON.parse(u) : null;
        } catch (e) {
            return null;
        }
    },

    setAuth(authResponse, rememberMe = true) {
        const storage = rememberMe ? localStorage : sessionStorage;
        storage.setItem(Config.STORAGE_KEY_TOKEN, authResponse.token);
        storage.setItem(Config.STORAGE_KEY_USER, JSON.stringify({
            id: authResponse.id,
            fullName: authResponse.fullName,
            email: authResponse.email,
            role: authResponse.role
        }));
    },

    isLoggedIn() {
        return !!this.getToken();
    },

    getRole() {
        const user = this.getUser();
        return user ? user.role : null;
    },

    logout() {
        localStorage.removeItem(Config.STORAGE_KEY_TOKEN);
        localStorage.removeItem(Config.STORAGE_KEY_USER);
        sessionStorage.removeItem(Config.STORAGE_KEY_TOKEN);
        sessionStorage.removeItem(Config.STORAGE_KEY_USER);
        window.location.href = 'login.html';
    },

    getDashboardUrl(role) {
        if (!role) role = this.getRole();
        if (role === 'ROLE_ADMIN') return 'admin-dashboard.html';
        if (role === 'ROLE_REVIEWER') return 'reviewer-dashboard.html';
        return 'student-dashboard.html';
    },

    requireAuth(allowedRoles = []) {
        if (!this.isLoggedIn()) {
            window.location.href = 'login.html?redirect=' + encodeURIComponent(window.location.pathname);
            return false;
        }

        const role = this.getRole();
        if (allowedRoles.length > 0 && !allowedRoles.includes(role)) {
            alert('Access denied. You do not have permission to view this page.');
            window.location.href = this.getDashboardUrl(role);
            return false;
        }
        return true;
    },

    redirectIfLoggedIn() {
        if (this.isLoggedIn()) {
            window.location.href = this.getDashboardUrl();
        }
    }
};

window.Auth = Auth;
