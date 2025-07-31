#!/usr/bin/env python3
import sqlite3
import sys

def check_database():
    try:
        # Connect to the database
        conn = sqlite3.connect('karl_database.db')
        cursor = conn.cursor()
        
        print("=== Database Structure ===")
        
        # Get table names
        cursor.execute("SELECT name FROM sqlite_master WHERE type='table';")
        tables = cursor.fetchall()
        print(f"Tables: {[table[0] for table in tables]}")
        
        # Check container_states table
        print("\n=== Container States ===")
        cursor.execute("SELECT user_id, length(state_data) as data_size, version, created_at, updated_at FROM container_states;")
        states = cursor.fetchall()
        
        if states:
            for state in states:
                print(f"User: {state[0]}, Data Size: {state[1]} bytes, Version: {state[2]}, Created: {state[3]}, Updated: {state[4]}")
                
            # Show actual state data (first few bytes)
            cursor.execute("SELECT state_data FROM container_states LIMIT 1;")
            state_data = cursor.fetchone()
            if state_data:
                data = state_data[0]
                print(f"State data preview (first 32 bytes): {data[:32] if data else 'None'}")
        else:
            print("No container states found.")
        
        # Check interaction_data table
        print("\n=== Interaction Data ===")
        cursor.execute("SELECT user_id, type, COUNT(*) as count FROM interaction_data GROUP BY user_id, type;")
        interactions = cursor.fetchall()
        
        if interactions:
            for interaction in interactions:
                print(f"User: {interaction[0]}, Type: {interaction[1]}, Count: {interaction[2]}")
                
            # Show latest interactions
            print("\n=== Latest Interactions ===")
            cursor.execute("SELECT type, details, timestamp FROM interaction_data ORDER BY timestamp DESC LIMIT 5;")
            latest = cursor.fetchall()
            for item in latest:
                print(f"Type: {item[0]}, Details: {item[1]}, Timestamp: {item[2]}")
        else:
            print("No interaction data found.")
            
        conn.close()
        
    except Exception as e:
        print(f"Error: {e}")

if __name__ == "__main__":
    check_database()
