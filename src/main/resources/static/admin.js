document.addEventListener('DOMContentLoaded', () => {
    const token = localStorage.getItem('authToken');
    if (!token || localStorage.getItem('userRole') !== 'ADMIN') {
        window.location.href = 'login.html';
        return;
    }

    const logoutBtn = document.getElementById('logout-btn');
    const registerForm = document.getElementById('register-form');
    const registerMessage = document.getElementById('register-message');

    logoutBtn.addEventListener('click', () => {
        localStorage.clear();
        window.location.href = 'login.html';
    });

    registerForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        registerMessage.textContent = '';

        const employeeData = {
            firstName: document.getElementById('firstName').value,
            lastName: document.getElementById('lastName').value,
            username: document.getElementById('username').value,
            password: document.getElementById('password').value,
            designation: document.getElementById('designation').value
        };

        try {
            const response = await fetch('/api/auth/employee/register', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify(employeeData)
            });

            if (response.ok) {
                registerMessage.textContent = 'Employee registered successfully!';
                registerMessage.className = 'success';
                registerForm.reset();
            } else {
                const errorText = await response.text();
                registerMessage.textContent = `Error: ${errorText}`;
                registerMessage.className = 'error';
            }
        } catch (error) {
            registerMessage.textContent = 'An unexpected error occurred.';
            registerMessage.className = 'error';
        }
    });
});