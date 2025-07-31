#!/usr/bin/env python3

import sqlite3
import os

# Database file path
db_path = "karl_database.db"

if not os.path.exists(db_path):
    print(f"Database file '{db_path}' not found!")
    exit(1)

print(f"Checking database: {db_path}")
print("=" * 50)

# Connect to database
conn = sqlite3.connect(db_path)
cursor = conn.cursor()

# Check container_states table
print("\n1. Container States Table:")
print("-" * 30)
try:
    cursor.execute("SELECT name FROM sqlite_master WHERE type='table' AND name='container_states'")
    if cursor.fetchone():
        cursor.execute("SELECT user_id, length(state_data) as data_size, version FROM container_states")
        states = cursor.fetchall()
        if states:
            for state in states:
                print(f"  User: {state[0]}")
                print(f"  State Data Size: {state[1]} bytes")
                print(f"  Version: {state[2]}")
        else:
            print("  No container states found")
    else:
        print("  Table 'container_states' not found")
except Exception as e:
    print(f"  Error checking container_states: {e}")

# Check interaction_data table
print("\n2. Interaction Data Table:")
print("-" * 30)
try:
    cursor.execute("SELECT name FROM sqlite_master WHERE type='table' AND name='interaction_data'")
    if cursor.fetchone():
        cursor.execute("SELECT COUNT(*) FROM interaction_data")
        count = cursor.fetchone()[0]
        print(f"  Total interactions: {count}")
        
        # Get recent interactions by type
        cursor.execute("""
            SELECT type, COUNT(*) as count 
            FROM interaction_data 
            GROUP BY type 
            ORDER BY count DESC 
            LIMIT 10
        """)
        types = cursor.fetchall()
        if types:
            print("  Interaction types:")
            for type_data in types:
                print(f"    {type_data[0]}: {type_data[1]} occurrences")
        
        # Get most recent interactions
        cursor.execute("""
            SELECT type, user_id, datetime(timestamp/1000, 'unixepoch') as formatted_time
            FROM interaction_data 
            ORDER BY timestamp DESC 
            LIMIT 5
        """)
        recent = cursor.fetchall()
        if recent:
            print("  Most recent interactions:")
            for interaction in recent:
                print(f"    {interaction[2]}: {interaction[0]} (user: {interaction[1]})")
                
    else:
        print("  Table 'interaction_data' not found")
except Exception as e:
    print(f"  Error checking interaction_data: {e}")

# Check table schemas
print("\n3. Table Schemas:")
print("-" * 20)
try:
    cursor.execute("SELECT name FROM sqlite_master WHERE type='table'")
    tables = cursor.fetchall()
    for table in tables:
        table_name = table[0]
        print(f"\n  Table: {table_name}")
        cursor.execute(f"PRAGMA table_info({table_name})")
        columns = cursor.fetchall()
        for col in columns:
            print(f"    {col[1]} ({col[2]})")
except Exception as e:
    print(f"  Error checking schemas: {e}")

conn.close()
print("\nDatabase check complete!")
