#!/usr/bin/env python3
"""
stamp-device.py — Batch flash FarmSense firmware with unique DEVICE_IDs

Usage:
  python stamp-device.py --start 1 --count 10 --port /dev/ttyUSB0

This script:
  1. Compiles the firmware with a unique DEVICE_ID define (FS-00001, FS-00002, ...)
  2. Flashes each device in sequence
  3. Logs the device ID and MAC address to a CSV file for your records

Requirements:
  pip install pyserial esptool
  arduino-cli installed and in PATH
"""

import argparse
import subprocess
import csv
import sys
import time
import serial
import os
from datetime import datetime

FIRMWARE_DIR = os.path.dirname(os.path.abspath(__file__)) + "/../src"
BUILD_DIR    = "/tmp/farmsense-build"
LOG_FILE     = f"device-log-{datetime.now().strftime('%Y%m%d-%H%M%S')}.csv"


def build(device_id: str, api_host: str) -> str:
    """Compile firmware with injected DEVICE_ID."""
    binary_path = f"{BUILD_DIR}/{device_id}/farmsense.bin"
    os.makedirs(os.path.dirname(binary_path), exist_ok=True)

    result = subprocess.run([
        "arduino-cli", "compile",
        "--fqbn", "esp32:esp32:esp32",
        "--build-property", f'compiler.cpp.extra_flags=-DDEVICE_ID=\\"{device_id}\\"',
        "--output-dir", os.path.dirname(binary_path),
        FIRMWARE_DIR
    ], capture_output=True, text=True)

    if result.returncode != 0:
        print(f"Build failed for {device_id}:\n{result.stderr}")
        sys.exit(1)

    print(f"✓ Built {device_id}")
    return binary_path


def flash(binary_path: str, port: str, baud: int = 921600):
    """Flash firmware to connected ESP32."""
    result = subprocess.run([
        "esptool.py",
        "--chip", "esp32",
        "--port", port,
        "--baud", str(baud),
        "write_flash",
        "--flash_mode", "dio",
        "--flash_freq", "80m",
        "--flash_size", "detect",
        "0x0", binary_path
    ], capture_output=True, text=True)

    if result.returncode != 0:
        print(f"Flash failed:\n{result.stderr}")
        return False

    print(f"✓ Flashed {binary_path}")
    return True


def get_mac(port: str, baud: int = 115200, timeout: int = 5) -> str:
    """Read MAC address from device after boot."""
    try:
        with serial.Serial(port, baud, timeout=timeout) as s:
            deadline = time.time() + timeout
            while time.time() < deadline:
                line = s.readline().decode(errors='replace').strip()
                if "WiFi MAC" in line or "mac" in line.lower():
                    return line.split()[-1]
        return "UNKNOWN"
    except Exception as e:
        return f"ERROR:{e}"


def main():
    parser = argparse.ArgumentParser(description="Batch flash FarmSense devices")
    parser.add_argument("--start",  type=int,  required=True, help="Starting device number")
    parser.add_argument("--count",  type=int,  default=1,     help="Number of devices to flash")
    parser.add_argument("--port",   type=str,  required=True, help="Serial port (e.g. /dev/ttyUSB0)")
    parser.add_argument("--prefix", type=str,  default="FS",  help="Device ID prefix")
    parser.add_argument("--api",    type=str,  default="https://api.farmsense.ma", help="API host")
    args = parser.parse_args()

    with open(LOG_FILE, "w", newline="") as f:
        writer = csv.writer(f)
        writer.writerow(["device_id", "mac", "flashed_at", "status"])

        for i in range(args.start, args.start + args.count):
            device_id = f"{args.prefix}-{i:05d}"
            print(f"\n{'='*50}")
            print(f"Flashing device {device_id} ({i - args.start + 1}/{args.count})")
            print(f"{'='*50}")
            print("Connect device and press ENTER...")
            input()

            # Build + flash
            binary = build(device_id, args.api)
            success = flash(binary, args.port)

            # Log
            status = "OK" if success else "FAILED"
            writer.writerow([device_id, "PENDING", datetime.now().isoformat(), status])
            f.flush()

            print(f"\nResult: {status}")
            if not success:
                print("Skipping to next device")

    print(f"\n✓ Done. Log saved to {LOG_FILE}")


if __name__ == "__main__":
    main()
