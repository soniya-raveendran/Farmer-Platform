/**
 * ROOTS Real-Time Notification System
 * Handles SSE connection and toast displays
 */

(function() {
    const userStr = localStorage.getItem("user");
    if (!userStr) return;

    const user = JSON.parse(userStr);
    const userId = user.id;
    const role = user.role.toString().toUpperCase().replace("ROLE_", "");

    // 1. Setup Toast Container
    const container = document.createElement("div");
    container.id = "toast-container";
    container.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        z-index: 9999;
        display: flex;
        flex-direction: column;
        gap: 10px;
    `;
    document.body.appendChild(container);

    // 2. Setup CSS for Toasts
    const style = document.createElement("style");
    style.innerHTML = `
        .toast {
            background: #ffffff;
            color: #333;
            padding: 16px 24px;
            border-radius: 12px;
            box-shadow: 0 10px 25px rgba(0,0,0,0.15);
            border-left: 5px solid #2e7d32;
            min-width: 300px;
            max-width: 450px;
            display: flex;
            align-items: center;
            gap: 15px;
            animation: slideIn 0.4s ease-out forwards;
            position: relative;
            overflow: hidden;
        }

        .toast-icon {
            font-size: 24px;
            color: #2e7d32;
        }

        .toast-content {
            flex: 1;
        }

        .toast-title {
            font-weight: 700;
            font-size: 15px;
            margin-bottom: 3px;
            color: #1b5e20;
        }

        .toast-message {
            font-size: 14px;
            color: #555;
            line-height: 1.4;
        }

        @keyframes slideIn {
            from { transform: translateX(120%); opacity: 0; }
            to { transform: translateX(0); opacity: 1; }
        }

        @keyframes fadeOut {
            from { transform: translateX(0); opacity: 1; }
            to { transform: translateX(120%); opacity: 0; }
        }

        .toast.fade-out {
            animation: fadeOut 0.4s ease-in forwards;
        }
    `;
    document.head.appendChild(style);

    // 3. Initialize SSE
    const eventSource = new EventSource(`http://localhost:8080/api/notifications/stream/${userId}?role=${role}`);

    eventSource.addEventListener("notification", function(event) {
        const data = JSON.parse(event.data);
        showToast(data.message);
    });

    eventSource.onerror = function() {
        console.warn("Notification stream lost. Reconnecting...");
    };

    function showToast(message) {
        const toast = document.createElement("div");
        toast.className = "toast";
        
        toast.innerHTML = `
            <div class="toast-icon">🔔</div>
            <div class="toast-content">
                <div class="toast-title">New Notification</div>
                <div class="toast-message">${message}</div>
            </div>
        `;

        container.appendChild(toast);

        // Auto-remove after 6 seconds
        setTimeout(() => {
            toast.classList.add("fade-out");
            setTimeout(() => {
                toast.remove();
            }, 400);
        }, 6000);
        
        // Instant click to close
        toast.onclick = () => {
             toast.classList.add("fade-out");
             setTimeout(() => toast.remove(), 400);
        };
    }

    console.log("ROOTS: Real-time notification system active.");
})();
