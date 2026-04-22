from page.AuthPage.register_page import RegisterPage
import random
import os

BASE_URL = os.getenv("BASE_URL", "http://localhost:5173")


def go_to_register_page(guest_page):
    register_page = RegisterPage(guest_page)
    register_page.goto(BASE_URL)
    register_page.assert_register_page_loaded()
    return register_page


def register_info_filler(
    guest_page,
    first_name,
    last_name,
    email,
    password,
    confirm_password,
    phone
):
    register_page = go_to_register_page(guest_page)
    register_page.register(
        first_name=first_name,
        last_name=last_name,
        email=email,
        password=password,
        confirm_password=confirm_password,
        phone=phone,
    )
    return register_page


def test_user_can_register_successfully(guest_page):
    """
    user inputs valid information and is redirected to login page
    """
    register_page = go_to_register_page(guest_page)

    unique_email = f"user{random.randint(100000, 999999)}@example.com"

    register_page.register(
        first_name="Test",
        last_name="User",
        email=unique_email,
        password="Password123!",
        confirm_password="Password123!",
        phone="1234567890",
    )
    register_page.submit()

    register_page.assert_redirected_to_login_page()


def test_invalid_email_can_not_register(guest_page):
    register_page = register_info_filler(
        guest_page,
        first_name="Test",
        last_name="User",
        email="invalid-email-format",
        password="11223344",
        confirm_password="11223344",
        phone="1234567890",
    )
    register_page.submit()
    register_page.assert_register_page_loaded()


def test_email_is_empty_can_not_register(guest_page):
    register_page = register_info_filler(
        guest_page,
        first_name="Test",
        last_name="User",
        email="",
        password="11223344",
        confirm_password="11223344",
        phone="1234567890",
    )
    register_page.submit()
    register_page.assert_email_field_empty()


def test_password_is_empty_can_not_register(guest_page):
    register_page = register_info_filler(
        guest_page,
        first_name="Test",
        last_name="User",
        email="test999@123.com",
        password="",
        confirm_password="11223344",
        phone="1234567890",
    )
    register_page.assert_password_field_empty()


def test_password_field_not_match_can_not_register(guest_page):
    register_page = register_info_filler(
        guest_page,
        first_name="Test",
        last_name="User",
        email="test999@123.com",
        password="1122",
        confirm_password="11223344",
        phone="1234567890",
    )
    register_page.assert_password_field_not_match()


def test_email_already_exists(guest_page):
    register_page = register_info_filler(
        guest_page,
        first_name="Test",
        last_name="User",
        email="employee@test.com",
        password="11223344",
        confirm_password="11223344",
        phone="1234567890",
    )
    register_page.submit()
    register_page.assert_email_already_exists()


def test_all_form_empty_register_button_still_click(guest_page):
    register_page = register_info_filler(
        guest_page,
        first_name="",
        last_name="",
        email="",
        password="",
        confirm_password="",
        phone="",
    )
    register_page.submit()
    register_page.assert_all_form_fields_empty()