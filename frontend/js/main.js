// ScholarConnect - Global Helpers & UI Utilities
const UI = {
    showToast(message, type = 'info') {
        let container = document.getElementById('toast-container');
        if (!container) {
            container = document.createElement('div');
            container.id = 'toast-container';
            document.body.appendChild(container);
        }

        const bgClass = {
            success: 'bg-success text-white',
            error: 'bg-danger text-white',
            warning: 'bg-warning text-dark',
            info: 'bg-primary text-white'
        }[type] || 'bg-primary text-white';

        const iconClass = {
            success: 'bi-check-circle-fill',
            error: 'bi-exclamation-octagon-fill',
            warning: 'bi-exclamation-triangle-fill',
            info: 'bi-info-circle-fill'
        }[type] || 'bi-info-circle-fill';

        const toastId = 'toast-' + Date.now();
        const toastEl = document.createElement('div');
        toastEl.className = `toast align-items-center ${bgClass} border-0 show shadow-lg mb-2`;
        toastEl.setAttribute('role', 'alert');
        toastEl.id = toastId;
        toastEl.innerHTML = `
            <div class="d-flex">
                <div class="toast-body d-flex align-items-center gap-2">
                    <i class="bi ${iconClass} fs-5"></i>
                    <div>${message}</div>
                </div>
                <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast"></button>
            </div>
        `;

        container.appendChild(toastEl);
        setTimeout(() => {
            toastEl.classList.remove('show');
            setTimeout(() => toastEl.remove(), 300);
        }, 4500);
    },

    formatCurrency(amount) {
        if (amount === undefined || amount === null) return '₹0';
        return new Intl.NumberFormat('en-IN', {
            style: 'currency',
            currency: 'INR',
            maximumFractionDigits: 0
        }).format(amount);
    },

    formatDate(dateStr) {
        if (!dateStr) return 'N/A';
        const d = new Date(dateStr);
        return d.toLocaleDateString('en-IN', { day: '2-digit', month: 'short', year: 'numeric' });
    },

    formatDateTime(dateStr) {
        if (!dateStr) return 'N/A';
        const d = new Date(dateStr);
        return d.toLocaleDateString('en-IN', { day: '2-digit', month: 'short', year: 'numeric', hour: '2-digit', minute: '2-digit' });
    },

    setupUserNavbar() {
        const user = Auth.getUser();
        const userDropdown = document.getElementById('userDropdownNav');
        const authButtons = document.getElementById('authNavButtons');

        if (user && userDropdown) {
            userDropdown.classList.remove('d-none');
            if (authButtons) authButtons.classList.add('d-none');

            const nameEl = document.getElementById('navUserName');
            const roleEl = document.getElementById('navUserRole');
            const avatarEl = document.getElementById('navUserAvatar');
            const dashLink = document.getElementById('navDashboardLink');

            if (nameEl) nameEl.textContent = user.fullName;
            if (roleEl) roleEl.textContent = user.role.replace('ROLE_', '');
            if (avatarEl) avatarEl.textContent = user.fullName.charAt(0).toUpperCase();
            if (dashLink) dashLink.href = Auth.getDashboardUrl(user.role);
        } else if (authButtons) {
            authButtons.classList.remove('d-none');
            if (userDropdown) userDropdown.classList.add('d-none');
        }
    },

    initSidebar() {
        const toggler = document.getElementById('sidebarToggler');
        const sidebar = document.querySelector('.sidebar');
        if (toggler && sidebar) {
            toggler.addEventListener('click', () => {
                sidebar.classList.toggle('show');
            });
        }
    }
};

document.addEventListener('DOMContentLoaded', () => {
    UI.setupUserNavbar();
    UI.initSidebar();
});

window.UI = UI;
