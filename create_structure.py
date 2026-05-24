#!/usr/bin/env python3
"""
Maven project structure creation script
Creates the complete directory structure for the travel planner project
"""

import os
import sys

def create_maven_structure():
    """Create the Maven project directory structure"""
    
    base_path = r'C:\Users\aarti\rt\programming\travel plan'
    
    # Change to base directory
    os.chdir(base_path)
    print(f"Working directory: {os.getcwd()}")
    
    # Define all directories to create
    directories = [
        'src/main/java/com/travelplanner/model',
        'src/main/java/com/travelplanner/dto',
        'src/main/java/com/travelplanner/repository',
        'src/main/java/com/travelplanner/service',
        'src/main/java/com/travelplanner/controller',
        'src/main/java/com/travelplanner/config',
        'src/main/java/com/travelplanner/security',
        'src/main/java/com/travelplanner/exception',
        'src/main/java/com/travelplanner/util',
        'src/main/resources',
        'src/test/java/com/travelplanner/service',
        'src/test/java/com/travelplanner/controller',
        'src/test/java/com/travelplanner/repository'
    ]
    
    print("\n" + "="*60)
    print("CREATING MAVEN PROJECT DIRECTORY STRUCTURE")
    print("="*60 + "\n")
    
    # Create all directories
    created_count = 0
    for directory in directories:
        try:
            os.makedirs(directory, exist_ok=True)
            print(f"✓ Created: {directory}")
            created_count += 1
        except Exception as e:
            print(f"✗ Failed to create {directory}: {e}")
    
    print(f"\n{'='*60}")
    print(f"Successfully created {created_count}/{len(directories)} directories")
    print("="*60 + "\n")
    
    # Verify structure - list src directory
    print("VERIFYING DIRECTORY STRUCTURE:")
    print("="*60 + "\n")
    
    print("Main directories created:")
    for root, dirs, files in os.walk('src'):
        level = root.replace('src', '').count(os.sep)
        indent = ' ' * 2 * level
        print(f'{indent}📁 {os.path.basename(root)}/')
        subindent = ' ' * 2 * (level + 1)
        for file in files:
            print(f'{subindent}📄 {file}')
    
    print("\n" + "="*60)
    print("✓ ALL DIRECTORIES CREATED SUCCESSFULLY!")
    print("="*60)
    
    return True

if __name__ == '__main__':
    try:
        create_maven_structure()
    except Exception as e:
        print(f"Error: {e}", file=sys.stderr)
        sys.exit(1)
