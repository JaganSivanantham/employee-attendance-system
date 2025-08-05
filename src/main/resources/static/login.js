document.addEventListener('DOMContentLoaded', () => {
    const loginForm = document.getElementById('loginForm');
    const employeeBtn = document.getElementById('employeeBtn');
    const adminBtn = document.getElementById('adminBtn');
    const errorMessage = document.getElementById('error-message');
    let loginType = 'employee'; // Default login type

    employeeBtn.addEventListener('click', () => {
        loginType = 'employee';
        employeeBtn.classList.add('active');
        adminBtn.classList.remove('active');
    });

    adminBtn.addEventListener('click', () => {
        loginType = 'admin';
        adminBtn.classList.add('active');
        employeeBtn.classList.remove('active');
    });

    loginForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        errorMessage.textContent = '';
        const username = document.getElementById('username').value;
        const password = document.getElementById('password').value;

        const url = `/api/auth/${loginType}/login`;

        try {
            const response = await fetch(url, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ username, password })
            });

            if (response.ok) {
                const data = await response.json();
                localStorage.setItem('authToken', data.token);
                localStorage.setItem('userRole', data.role);
                if (data.role === 'ADMIN') {
                    window.location.href = 'admin-dashboard.html';
                } else {
                    localStorage.setItem('employeeId', data.employeeId);
                    localStorage.setItem('employeeName', data.name);
                    window.location.href = 'employee-dashboard.html';
                }
            } else {
                const errorText = await response.text();
                errorMessage.textContent = errorText || 'Login failed. Please check your credentials.';
            }
        } catch (error) {
            errorMessage.textContent = 'An error occurred. Please try again later.';
            console.error('Login error:', error);
        }
    });
});