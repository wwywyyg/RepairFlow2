from playwright.sync_api import sync_playwright

def save_customer_state():
    with sync_playwright() as p:
        browser = p.chromium.launch(headless=False)
        context = browser.new_context()
        page = context.new_page()

        # 1️go to login page
        page.goto("http://localhost:5173/login")

        # 2️fill info
        page.fill("input[name='email']", "customer@test.com")
        page.fill("input[name='password']", "11223344")

        # 3️login
        page.click("button:has-text('Login')")

        # 4️wait
        page.wait_for_url("http://localhost:5173/dashboard")

        #  save status
        context.storage_state(path="customer_auth.json")

        browser.close()

if __name__ == "__main__":
    save_customer_state()